package com.cj.witbasics.entity;

import java.util.Date;

/**
 * 角色导出实体类
 * Created by XD on 2018/5/19.
 */
public class AdminRoleExport {
    /**
     * 管理员角色表
     */
    private String id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色分类，0：系统用户 	1: 系统管理员 	2: 学校管理员 	3: 老师 	4：学生 	5：家长 	6：其他
     */
    private String type;

    /**
     * 状态（1-正常，0-已删除）
     */
    private String state;

    /**
     * 创建时间
     */
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        if("0".equals(type)){
            this.type = "系统用户";
        }
        if("1".equals(type)){
            this.type = "系统管理员";
        }
        if("2".equals(type)){
            this.type = "学校管理员";
        }
        if("3".equals(type)){
            this.type = "老师";
        }
        if("4".equals(type)){
            this.type = "学生";
        }
        if("5".equals(type)){
            this.type = "家长";
        }
        if("6".equals(type)){
            this.type = "其他";
        }

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        System.out.println("state--------------------" + state);
        if("0".equals(state)){
            this.state = "已删除";
        }if("1".equals(state)) {
            this.state = "正常";
        }
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

}
