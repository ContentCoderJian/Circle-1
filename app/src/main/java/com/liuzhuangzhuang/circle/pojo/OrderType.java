package com.liuzhuangzhuang.circle.pojo;

import java.util.ArrayList;

/**
 * 订单类型
 */
public class OrderType implements Cloneable {

    private Integer id;                     // 分类id
    private String typeName;                // 分类名称
    private Integer parentId;               // 父类id
    private String parentTypeName;          // 父类分类名称
    private String orderCategory;           // 便民服务 / 生活配送
    private String imageName;               // 对应分类图片名称
    private int imageResId;                 // 图片资源id
    private ArrayList<OrderType> children;  // 子分类

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getParentTypeName() {
        return parentTypeName;
    }

    public void setParentTypeName(String parentTypeName) {
        this.parentTypeName = parentTypeName;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


    public ArrayList<OrderType> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<OrderType> children) {
        this.children = children;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

}

