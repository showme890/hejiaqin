package com.chinamobile.hejiaqin.business.model.contacts;

import com.customer.framework.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人详情
 */
public class ContactsInfo {
    private String name;

    private List<NumberInfo> numberLst = new ArrayList<NumberInfo>();

    private String nameInPinyin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NumberInfo> getNumberLst() {
        return numberLst;
    }

    public void addNumber(NumberInfo numberInfo) {
        if (null == numberInfo) {
            return;
        }
        this.numberLst.add(numberInfo);
    }

    public String getNameInPinyin() {
        return nameInPinyin;
    }

    public void setNameInPinyin(String nameInPinyin) {
        this.nameInPinyin = nameInPinyin;
    }

    /**
     * 获取分组的名称，目前是以A-Z的规则进行分组
     *
     * @return
     */
    public String getGroupName() {
        if (StringUtil.isNullOrEmpty(this.nameInPinyin)) {
            return "#";
        }

        char firstChar = this.nameInPinyin.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            return String.valueOf(firstChar);
        }
        return "#";
    }

    public boolean isMatch(String input) {
        if (StringUtil.isNullOrEmpty(input)) {
            return true;
        }

        if (name.contains(input)) {
            return true;
        }

        if (nameInPinyin.contains(input.toUpperCase())) {
            return true;
        }

        for (NumberInfo numberInfo : numberLst) {
            if (numberInfo.isMatch(input)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ContactsInfo{" +
                "name='" + name + '\'' +
                ", numberLst='" + numberLst + '\'' +
                ", nameInPinyin='" + nameInPinyin + '\'' +
                '}';
    }
}
