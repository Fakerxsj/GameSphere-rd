package com.xsj.constants;

/**
 * 角色常量类
 * 用于管理用户角色 ID 的定义
 */
public class RoleConstants {

    /**
     * 管理员角色 ID
     */
    public static final Long ADMIN = 1L;

    /**
     * 普通用户角色 ID
     */
    public static final Long NORMAL_USER = 2L;

    /**
     * 判断是否为管理员
     * @param roleId 角色 ID
     * @return true-管理员，false-非管理员
     */
    public static boolean isAdmin(Long roleId) {
        return ADMIN.equals(roleId);
    }

    /**
     * 判断是否为普通用户
     * @param roleId 角色 ID
     * @return true-普通用户，false-非普通用户
     */
    public static boolean isNormalUser(Long roleId) {
        return NORMAL_USER.equals(roleId);
    }

    /**
     * 获取角色名称
     * @param roleId 角色 ID
     * @return 角色名称
     */
    public static String getRoleName(Long roleId) {
        if (ADMIN.equals(roleId)) {
            return "管理员";
        } else if (NORMAL_USER.equals(roleId)) {
            return "普通用户";
        }
        return "未知角色";
    }
}
