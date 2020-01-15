package com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant;

import java.io.Serializable;

/**
 * @author Michael Soyma (Created on 10/9/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class Code implements Serializable {
    private String id;
    private String deal_order_id;
    private String deal_code;
    private String is_used;
    private String used_on;

    public String getId() {
        return id;
    }

    public boolean isUsed() {
        return is_used.equals("1");
    }

    public String getDealCode() {
        return deal_code;
    }
}
