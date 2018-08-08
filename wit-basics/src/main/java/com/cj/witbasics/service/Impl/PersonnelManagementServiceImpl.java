package com.cj.witbasics.service.Impl;

import com.cj.witbasics.entity.*;
import com.cj.witbasics.mapper.AdminInfoMapper;
import com.cj.witbasics.mapper.DepartmentAdminMapper;
import com.cj.witbasics.mapper.DepartmentInfoMapper;
import com.cj.witbasics.service.CloudService;
import com.cj.witbasics.service.PersonnelManagementService;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.SLIDUtil;
import com.cj.witcommon.entity.SynBasicInformation;
import com.cj.witcommon.utils.TimeToString;
import com.cj.witcommon.utils.common.FileUtil;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witcommon.utils.excle.ImportExeclUtil;
import com.cj.witcommon.utils.util.Md5Utils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class PersonnelManagementServiceImpl implements PersonnelManagementService {
    @Autowired
    private AdminInfoMapper adminInfoMapper;
    @Autowired
    private DepartmentInfoMapper departmentInfoMapper;

    @Autowired
    private DepartmentAdminMapper departmentAdminMapper;

    @Autowired
    private CloudService cloudService;


    @Value("${default_admin_pass}")
    String defaultAdminPass;

    @Value("${web.upload-path}")
    String path;

    /**
     * 根据部门和模糊名字进行查询
     */
    public List<AdminInfo> selectByDepartAndName(AdminInfo adminInfo){
        return adminInfoMapper.selectByDepartAndName(adminInfo);
    }

    @Value("${teacher_staff_prefix}")
    String teacherStaffPrefix;

    @Value("${school_id}")
    String schoolId;
    /**
     * 生成最新职工编号
     */
    public String productMaxStaffNumber(){
        //获取职工编号最大值
//        Long maxStaffNumber= adminInfoMapper.selectMaxStaffNumber()+1L;
//        return teacherStaffPrefix+"000"+maxStaffNumber;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return teacherStaffPrefix+schoolId+sdf.format(new Date());
    }

    /**
     * 创建管理员Admin
     * 以员工编号为账号，
     */
    public Long createAdmin(Admin admin){
        return adminInfoMapper.insertAdminSelective(admin);
    }
    /**
     * 创建人事信息详情
     */
    public Integer createAdminInfo(AdminInfo adminInfo,HttpServletRequest request,String adminType) {
        System.out.println("===============================================================");
        System.out.println(adminInfo);
        System.out.println("===============================================================");

        //批量导入时，部门ID不存在，进行部门匹配
        if(adminInfo.getTheDepartmentId() == null || adminInfo.getTheDepartmentId() < 0 ){
            System.out.println("进入");
            //查询部门信息
            List<DepartmentInfo> departmentInfos = departmentInfoMapper.getAllDepartmentInfo();
            for(DepartmentInfo departmentInfo : departmentInfos){
                //部门名称匹配
                if(departmentInfo.getdName().equals(adminInfo.getTheDepartment())){
                    adminInfo.setTheDepartmentId(departmentInfo.getId().intValue());
                    break;
                }
            }

        }

        //部门信息匹配后，部门ID还是不存在，给与提示
        if(adminInfo.getTheDepartmentId() == null || adminInfo.getTheDepartmentId() < 0 ){
            return -1;
        }

        //根据教职工编号 检查信息是否已存在，如果已存在，则覆盖
        Long adminId0 = adminInfoMapper.findAdminIdByStaffNum(adminInfo.getStaffNumber());
        if(adminId0 != null && adminId0 > 0){
            //修改部门关系
            DepartmentAdmin departmentAdmin = new DepartmentAdmin();
            departmentAdmin.setdId(adminInfo.getTheDepartmentId());
            departmentAdmin.setAdminId(adminId0);
            departmentAdminMapper.updateDidByAdminId(departmentAdmin);

            adminInfo.setAdminId(adminId0);
            int i = adminInfoMapper.updateByPrimaryKeySelective(adminInfo);
            return i;
        }


        //当前时间
        Date date=new Date();
        //存储结果
//        Integer result=-1;

        //管理员表插入信息
        Admin admin=new Admin();
        //管理员账号
        if(adminInfo.getAdminName() != null && adminInfo.getAdminName().length()>0){  //批量导入时有管理员账号

            admin.setAdminName(adminInfo.getAdminName());
        }else {  //默认将教职工编号作为管理员账号
            admin.setAdminName(adminInfo.getStaffNumber());
        }
        //添加管理员密码
        if(adminInfo.getAdminPass() != null && adminInfo.getAdminPass().length()>0){
            admin.setAdminPass(adminInfo.getAdminPass());
        }else {
            admin.setAdminPass(Md5Utils.MD5Encode(defaultAdminPass,"UTF-8",false));
        }

        //角色ID，如果为空则默认为5：科目教师
        if(adminInfo.getRoleId() != null && adminInfo.getRoleId()>0){
            admin.setRoleId(adminInfo.getRoleId());
        }else {
            admin.setRoleId(5l);
        }

        //根据adminRoleId查询Role信息
        AdminRole adminRole = adminInfoMapper.findAdminRoleByAdminRoleId(admin.getRoleId());



        //账户分类
        if(adminType == null || adminType.length()==0){
            //默认为2
            admin.setAdminType("2");
        }else {
            admin.setAdminType(adminType);
        }

        //创建时间
        admin.setCreateTime(date);
        Long adminId = 0l;
        int nums = 0;  //本地添加成功条数
        //TODO:云端创建教职工账号
        if (cloudService.cloudRegisterPM(request,admin,adminRole)){

            Long newUserId = (Long) request.getSession().getAttribute("newUserId");
            if(newUserId != null && newUserId > 0){
                //设置adminId为云端注册的ID
                admin.setId(newUserId);
            }
            adminInfoMapper.insertAdminSelective(admin);
            adminId = admin.getId();

            adminInfo.setAdminId(adminId);
            //创建时间
            adminInfo.setCreateTime(date);
            String newUserUUID = (String) request.getSession().getAttribute("newUserUUID");
            //设置用户uuid为云端注册的uuid
            if(newUserUUID != null && newUserUUID.length() > 0){
                adminInfo.setAdminUuid(newUserUUID);
            }else {  //根据角色类型生成uuid
                adminInfo.setAdminUuid(SLIDUtil.newUUID(adminRole.getType()));
            }
                    Long founderId = (Long) request.getSession().getAttribute("adminId");
//            Long founderId = 0l;  //开发环境，假设操作员id为0
            adminInfo.setFounderId(founderId);

            SynBasicInformation synBasicInformation = new SynBasicInformation();
            synBasicInformation.setName(adminInfoMapper.findAdminNameByadminId(adminId));  //用户名
            synBasicInformation.setNickName(adminInfo.getAdminNick());  //昵称
            synBasicInformation.setEnglishName(adminInfo.getEnglishName());  //英文名
            synBasicInformation.setBirthday(adminInfo.getBirthDate());  //生日
            synBasicInformation.setSex(adminInfo.getGender());  //性别
            synBasicInformation.setAvatar(adminInfo.getAdminHead());  //头像

            //TODO:云端同步教职工基础信息
            if(cloudService.cloudSynchronization(request,synBasicInformation)){
                System.out.println("============>>"+adminInfo);
                nums=adminInfoMapper.insertSelective(adminInfo);

                //添加人事-部门-关联表信息
                DepartmentAdmin departmentAdmin = new DepartmentAdmin();
                departmentAdmin.setAdminId(adminId);
                departmentAdmin.setdId(adminInfo.getTheDepartmentId());
                departmentAdminMapper.insertSelective(departmentAdmin);
            }
        }

        return nums;
    }

    //批量处理adminInfo数据导入
    @Override
    public Map createAdminInfos(MultipartFile multipartFile, HttpServletRequest request) throws Exception {

        String fileName = multipartFile.getOriginalFilename();
        InputStream in = multipartFile.getInputStream();

        Workbook wb = ImportExeclUtil.chooseWorkbook(fileName, in);
        int num = wb.getNumberOfSheets();
        System.out.println("sheet数量==>>"+num);

        //接收多个sheet中的数据
        List<AdminInfo> adminInfoList = new ArrayList<>();

        for (int i = 0;i<num ;i++) {

            //读取对象列表的信息
            AdminInfo adminInfo = new AdminInfo();

            //第二行开始，到倒数第一行结束（总数减去0行）
            List<AdminInfo> readDateListT = ImportExeclUtil.readDateListT(wb,adminInfo , 2, 0,i);
            System.out.println(readDateListT);


            adminInfoList.addAll(readDateListT);
        }

        int nums = 0;  //教职工信息导入成功数量
        int i = 0;
        List msgList = new ArrayList();
        Map map = new HashMap();
        for (AdminInfo adminInfo : adminInfoList) {

            //调用创建人事信息接口
            i = createAdminInfo(adminInfo,request,"");
            if(i == 1){
                msgList.add(adminInfo.getFullName()+"("+adminInfo.getStaffNumber()+") 处理成功。");
                nums += i;
            }else if(i == -1){
                msgList.add(adminInfo.getFullName()+"("+adminInfo.getStaffNumber()+")导入失败   "+adminInfo.getTheDepartment()+" 部门不存在。");

            }else {
                msgList.add(adminInfo.getFullName()+"("+adminInfo.getStaffNumber()+") 处理失败。");

            }

        }

        map.put("nums",nums);
        map.put("msgList",msgList);


        return map;



    }

    /**
     * 删除员工 ，修改状态为0——已删除
     * 同时删除职工-人事关系
     * 同时删除账号
     */
    @Transactional
    @Override
    public Integer deleteStaff(List<Long> adminIds, HttpServletRequest request) {
                Long founderId = (Long) request.getSession().getAttribute("adminId");
//        Long founderId = 0l;  //开发环境，假设操作员id为0
        HashMap<String,Object> params;
        Date date=new Date();
        int i;  //接收处理成功条数
        List<Map> list = new ArrayList<>();  //
        for (Long adminId:adminIds) {
            params=new HashMap<>();
            params.put("adminId",adminId);
            params.put("operator_id",founderId);
            params.put("updateTime",date);
            params.put("adminState","0");

            list.add(params);

        }

        return ApiCode.SUCCESS;
    }
    /**
     *修改密码adminPass updateTime adminId
     */
    @Transactional
    public Integer updateAdminPass(Long[] adminIds, String adminPass){
        Date date=new Date();
        HashMap<String,Object> params;
        int i;
        for (Long adminId:adminIds ){
            params=new HashMap<>();
            params.put("adminPass",adminPass);
            params.put("updateTime",date);
            params.put("adminId",adminId);
            i = adminInfoMapper.updateAdminPass(params);
            if (i==-1){
                return ApiCode.FAIL;
            }
        }
        return ApiCode.SUCCESS;
    }
    /**
     * 通过adminId查询员工信息
     */
    public AdminInfo selectAdminInfoById(Long adminId){
        return adminInfoMapper.selectByadminId(adminId);
    }
    /**
     * 修改教职工信息
     */
    @Transactional
    public Integer updateAdminInfo(AdminInfo adminInfo,HttpServletRequest request){
                Long founderId = (Long) request.getSession().getAttribute("adminId");
//        Long founderId = 0l;  //开发环境，假设操作员id为0

        adminInfo.setFounderId(founderId);

        Date updateTime=new Date();
        int i = 0;

        SynBasicInformation synBasicInformation = new SynBasicInformation();
        synBasicInformation.setName(adminInfoMapper.findAdminNameByadminId(adminInfo.getAdminId()));  //用户名
        synBasicInformation.setNickName(adminInfo.getAdminNick());  //昵称
        synBasicInformation.setEnglishName(adminInfo.getEnglishName());  //英文名
        synBasicInformation.setBirthday(adminInfo.getBirthDate());  //生日
        synBasicInformation.setSex(adminInfo.getGender());  //性别
        synBasicInformation.setAvatar(adminInfo.getAdminHead());  //头像

        //TODO:云端同步修改教职工信息
        if (cloudService.cloudSynchronization(request,synBasicInformation)){

            HashMap<String,Object> hashMap=new HashMap<>();

            hashMap.put("updateTime",updateTime);
            hashMap.put("adminId",adminInfo.getAdminId());
            //根据adminId更新admin表信息
            i += adminInfoMapper.updateAdminTime(hashMap);

            //更新adminInfo表信息
            i += adminInfoMapper.updateByPrimaryKeySelective(adminInfo);
        }


        if (i<2){
            return ApiCode.FAIL;
        }


        return ApiCode.SUCCESS;
    }
    /**
     * 锁定id
     */
    @Transactional
    public Integer lockingAdminInfo(Long adminId){
        Date date=new Date();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("adminId",adminId);
        hashMap.put("adminState","-1");
        hashMap.put("updateTime",date);
        int i;
        i=adminInfoMapper.lockingAdminByAdminId(hashMap);
        if (i==-1){
            return ApiCode.FAIL;
        }
        return ApiCode.SUCCESS;
    }

    /**
     * 新增部门
     */
    @Transactional
    public Integer addDepartmentInfo(DepartmentInfo departmentInfo,HttpServletRequest request){
        Long founderId = (Long) request.getSession().getAttribute("adminId");//操作员ID
        Date date=new Date();
        departmentInfo.setFounderId(founderId);
        departmentInfo.setCreateTime(date);
        departmentInfo.setState("1");
        int i;
        i=departmentInfoMapper.insertSelective(departmentInfo);

        //检查 此用户是否已经是其他部门的人员
        DepartmentAdmin departmentAdmin = departmentAdminMapper.findDepartmentAdminByAdminId(departmentInfo.getdLeaderId());
        if(departmentAdmin == null){
            departmentAdmin = new DepartmentAdmin();
            //将部门领导添加到部门内
            departmentAdmin.setdId(departmentInfo.getId().intValue());
            departmentAdmin.setAdminId(departmentInfo.getdLeaderId());

            departmentAdminMapper.insertSelective(departmentAdmin);

        }else {
            //修改 adminId --部门关联信息
            departmentAdmin.setdId(departmentInfo.getId().intValue());
            departmentAdminMapper.updateDidByAdminId(departmentAdmin);
        }

        //更新adminInfo 信息

        Map map = new HashMap();
        map.put("adminId",departmentInfo.getdLeaderId());  //adminID
        map.put("did",departmentInfo.getId());  //部门ID
        map.put("theDepartment",departmentInfo.getdName());  //部门名称
        map.put("operatorId",founderId);  //操作员ID
        adminInfoMapper.updateStaffDepartment(map);


        if (i==-1){
            return ApiCode.FAIL;
        }
        return ApiCode.SUCCESS;
    }
    /**
     * 根据部门ID、姓名分页、条件查找部门人员信息
     */
    public Pager selectAdminInfoByDepartment(Pager p){


        //查询总条数
        p.setRecordTotal(adminInfoMapper.selectAdminInfoByDepartmentTotal(p));

        //查询结果
        p.setContent(adminInfoMapper.selectAdminInfoByDepartment(p));

        return p;
    }
    /**
     * 根据部门ID、姓名分页、条件查找部门人员信息
     */
    public Pager selectAdminInfoAndDepartment(Pager p){


        //查询总条数
        p.setRecordTotal(adminInfoMapper.selectAdminInfoAndDepartmentTotal(p));

        //查询结果
        p.setContent(adminInfoMapper.selectAdminInfoAndDepartment(p));

        return p;
    }

    /**
     * 根据姓名模糊查询没有担任部门领导的人
     */
    @Override
    public Pager findAllDepartmentalLeadership(Pager pager) {
        pager.setRecordTotal(adminInfoMapper.findAllDepartmentalLeadershipTotal(pager));
        pager.setContent(adminInfoMapper.findAllDepartmentalLeadership(pager));

        return pager;
    }

    /**
     * 根据姓名进行模糊查询
     */
    public Pager selectAdminInfoByVague(Pager p){

        //查询总条数
        p.setRecordTotal(adminInfoMapper.selectAdminInfoByVagueTotal(p));

        //查询结果
        p.setContent(adminInfoMapper.selectAdminInfoByVague(p));
        return p;
    }
    /**
     * 修改员工所在部门
     */
    public Integer updateStaffDepartment(List<Long> adminIds,Long did, String theDepartment, HttpServletRequest request){
        int i = 0;
        Long operatorId = (Long) request.getSession().getAttribute("adminId");  //操作员ID
//        Long operatorId = 0l;  //操作员ID,开发时假设为0

        //更新adminInfo表中部门名称 和 DepartmentAdmin表中 adminId-部门ID关系
        Map map = new HashMap();
        //查询部门领导ID集合
        List<Long> departmentAdminIds = departmentInfoMapper.findAllDepartmentAdminIds();
        for (Long adminId:adminIds){
            map.put("adminId",adminId);  //adminID
            map.put("did",did);  //部门ID
            map.put("theDepartment",theDepartment);  //部门名称
            map.put("operatorId",operatorId);  //操作员ID
            for (Long dLeaderId : departmentAdminIds){
                if(adminId == dLeaderId){  //如果被操作的用户是部门领导
                    //则清除此部门领导信息
                    departmentInfoMapper.updateDepartmentAdminId(dLeaderId);


                }
            }


            //检查此用户是否存在部门信息关联
            DepartmentAdmin departmentAdmin =  departmentAdminMapper.findDepartmentAdminByAdminId(adminId);
            if(departmentAdmin == null){
                departmentAdmin = new DepartmentAdmin();
                departmentAdmin.setAdminId(adminId);
                departmentAdmin.setdId(did.intValue());
                departmentAdminMapper.insertSelective(departmentAdmin);
            }else {
                departmentAdmin.setdId(did.intValue());
                departmentAdminMapper.updateDidByAdminId(departmentAdmin);
            }

            //修改 adminInfo 信息
            i += adminInfoMapper.updateStaffDepartment(map);

            if (i==-1){
                return ApiCode.FAIL;
            }
        }

        return ApiCode.SUCCESS;
    }
    /**
     * 查询部门信息
     */
    public DepartmentInfo selectDepartmentInfoById(Long id){
        return departmentInfoMapper.selectDepartmentInfoById(id);

    }

    private static int num = 0;
    @Override
    public String getDNumber() {
        num++;
        return "A"+TimeToString.DateToStr(new Date())+num;
    }

    @Override
    public List<DepartmentInfo> findAllDepartmentInfo() {
        return departmentInfoMapper.getAllDepartmentInfo();
    }

    @Override
    public int updateDepartmentInfo(DepartmentInfo departmentInfo) {
        //校验部门名字是否重复
        DepartmentInfo departmentInfo1 =  departmentInfoMapper.findDepartmentInfoByName(departmentInfo);

        if(departmentInfo1 != null){  //该部门名称已存在

            return -1;
        }else {
            AdminInfo adminInfo = new AdminInfo();
            //查询此部门原信息
            DepartmentInfo oldDepartmentInfo = departmentInfoMapper.selectByPrimaryKey(departmentInfo.getId());

            //如果部门原领导ID != 此部门新领导ID
            if(oldDepartmentInfo.getdLeaderId() != departmentInfo.getdLeaderId()){
                //将部门原领导信息移除
                departmentAdminMapper.deleteDepartmentAdminByAdminId(oldDepartmentInfo.getdLeaderId());
                //移除adminInfo部门信息
                adminInfo.setAdminId(oldDepartmentInfo.getdLeaderId());
                adminInfo.setTheDepartmentId(0);
                adminInfo.setTheDepartment("");
                adminInfoMapper.updateByPrimaryKeySelective(adminInfo);


                //将部门领导加入到此部门
                //根据 adminId 查询部门关联信息
                DepartmentAdmin departmentAdmin = departmentAdminMapper.findDepartmentAdminByAdminId(departmentInfo.getdLeaderId());
                if(departmentAdmin == null ){  //此用户之前不存在任何部门
                    departmentAdmin = new DepartmentAdmin();
                    departmentAdmin.setAdminId(departmentInfo.getdLeaderId());
                    departmentAdmin.setdId(departmentInfo.getId().intValue());
                    //将部门新领导添加到数据库
                    departmentAdminMapper.insertSelective(departmentAdmin);

                }else {
                    departmentAdmin.setAdminId(departmentInfo.getdLeaderId());
                    departmentAdmin.setdId(departmentInfo.getId().intValue());
                    //将部门新领导数据修改
                    departmentAdminMapper.updateDidByAdminId(departmentAdmin);
                }



            }

            //跟新 adminInfo 部门信息
            adminInfo.setAdminId(departmentInfo.getdLeaderId());
            adminInfo.setTheDepartmentId(departmentInfo.getId().intValue());
            adminInfo.setTheDepartment(departmentInfo.getdName());
            adminInfoMapper.updateByPrimaryKeySelective(adminInfo);

            //根据部门ID修改部门信息
            return departmentInfoMapper.updateByPrimaryKeySelective(departmentInfo);
        }

    }

    @Override
    public void downloadTeacherTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //文件名
        String fileName ="schoolTeacher.xls";
        new FileUtil().download(request,response,path,fileName);
    }

    /**
     * 根据部门ID删除部门
     */
    @Override
    public int deleteDepartmentInfo(Long id) {
        //检查部门下有没有成员
        int num = departmentAdminMapper.findPersonnelById(id);
        if(num > 0){
            return -1;
        }
        //根据部门ID删除部门
        return departmentInfoMapper.deleteByPrimaryKey(id);

    }

}
