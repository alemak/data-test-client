package com.netaporter.test.client.product.pojos;

import java.lang.Comparable;
import java.lang.Integer;
import java.lang.String;

public class SearchableProduct implements Comparable {

    private Integer id;
    private String title;
    private String designer;

    public SearchableProduct(Integer id, String title, String designer) {
        this.id = id;
        this.title = title;
        this.designer = designer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesigner() {
        return designer;
    }

    public void setDesigner(String designer) {
        this.designer = designer;
    }

    @Override
    public int compareTo(java.lang.Object o) {
        SearchableProduct sp = (SearchableProduct) o;
        return this.getId().compareTo(sp.getId());
    }
}