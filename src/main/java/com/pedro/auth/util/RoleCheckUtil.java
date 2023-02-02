package com.pedro.auth.util;

import com.pedro.auth.common.RoleRuleEnum;

import java.util.List;

public class RoleCheckUtil {

    /**
     * 根据实际权限， 所需权限，RoleRuleEnum.type检查是否通过
     *
     * @param roleList      拥有的权限列表
     * @param needRoles     所需的权限列表
     * @param ruleCheckType 比较模式
     * @return
     */
    public static boolean checkRole(List<String> roleList, List<String> needRoles, String ruleCheckType) {

        if (ruleCheckType.equals(RoleRuleEnum.NEED_ONE.getType())) {
            // 只需一个
            for (String needRole : needRoles) {
                if (roleList.contains(needRole)) {
                    return true;
                }
            }
            return false;
        } else if (ruleCheckType.equals(RoleRuleEnum.NEED_ALL.getType())) {
            // 需要全部
            for (String needRole : needRoles) {
                if (!roleList.contains(needRole)) {
                    return false;
                }
            }
            return true;
        } else {
            throw new RuntimeException("[pedroAuth]传入验证方法的角色匹配规则类型错误");
        }

    }
}
