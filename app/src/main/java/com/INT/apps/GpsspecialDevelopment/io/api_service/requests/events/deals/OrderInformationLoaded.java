package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.Order;

/**
 * @author Michael Soyma (Created on 9/11/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class OrderInformationLoaded {

    private Order order;

    public OrderInformationLoaded(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
