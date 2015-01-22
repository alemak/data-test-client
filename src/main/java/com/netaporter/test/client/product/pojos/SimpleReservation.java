package com.netaporter.test.client.product.pojos;

import java.util.Date;

/**
 * Created by a.makarenko on 07/11/2014.
 */
public class SimpleReservation {

        private Integer id;
        private Integer customerId;
        private String sku;
        private Integer reservedQuantity;
        private Integer redeemedQuantity;
        private Date createdDts;
        private Date lastUpdatedDts;
        private String createdBy;
        private String lastUpdatedBy;
        private String status;
        private Date expiryDate;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public Integer getReservedQuantity() {
            return reservedQuantity;
        }

        public void setReservedQuantity(Integer reservedQuantity) {
            this.reservedQuantity = reservedQuantity;
        }

        public Integer getRedeemedQuantity() {
            return redeemedQuantity;
        }

        public void setRedeemedQuantity(Integer redeemedQuantity) {
            this.redeemedQuantity = redeemedQuantity;
        }

        public Date getCreatedDts() {
            return createdDts;
        }

        public void setCreatedDts(Date createdDts) {
            this.createdDts = createdDts;
        }

        public Date getLastUpdatedDts() {
            return lastUpdatedDts;
        }

        public void setLastUpdatedDts(Date lastUpdatedDts) {
            this.lastUpdatedDts = lastUpdatedDts;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getLastUpdatedBy() {
            return lastUpdatedBy;
        }

        public void setLastUpdatedBy(String lastUpdatedBy) {
            this.lastUpdatedBy = lastUpdatedBy;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Date getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }


}
