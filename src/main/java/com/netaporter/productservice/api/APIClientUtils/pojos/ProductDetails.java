package com.netaporter.productservice.api.APIClientUtils.pojos;

import java.util.List;

/**
 * Created by a.makarenko on 7/1/14.
 */
public class ProductDetails {
        private String designerName;
        private String title;
        private Double price;
        private Double salePrice;
        private String currency;
        private String editorsNotes;
        private String details;
        private String sizeFit;
        private Boolean onSale;



    private List<Integer> categories;

    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEditorsNotes() {
        return editorsNotes;
    }

    public void setEditorsNotes(String editorsNotes) {
        this.editorsNotes = editorsNotes;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getSizeFit() {
        return sizeFit;
    }

    public void setSizeFit(String sizeFit) {
        this.sizeFit = sizeFit;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }
}
