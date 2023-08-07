package com.example.springbootmongodb.common.data.shipment.ghtk;

public class GHTKEndpoints {
    public static final String GHTK_BASE_URL = "https://services-staging.ghtklab.com";
    public static final String GHTK_CREATE_SHIPMENT_ROUTE = GHTK_BASE_URL + "/services/shipment/order";
    public static final String GHTK_CALCULATE_DELIVERY_FEE_ROUTE = GHTK_BASE_URL + "/services/shipment/fee";
    public static final String GHTK_GET_SHIPMENT_STATE_ROUTE = GHTK_BASE_URL + "/services/shipment/v2/S1.A1.17373471";
    public static final String GHTK_CANCEL_SHIPMENT_ROUTE = GHTK_BASE_URL + "/services/shipment/cancel/S1.17373471";
    public static final String GHTK_PRINT_SHIPMENT_LABEL_ROUTE = GHTK_BASE_URL + "/services/label/S1.8663516";
    public static final String GHTK_GET_PICK_UP_ADDRESSES_ROUTE = GHTK_BASE_URL + "/services/shipment/list_pick_add";
    public static final String GHTK_GET_LV4_ADDRESSES_ROUTE = GHTK_BASE_URL + "/services/address/getAddressLevel4";
    public static final String GHTK_GET_SHIPMENT_PRODUCT_INFO_ROUTE = GHTK_BASE_URL + "/services/kho-hang/thong-tin-san-pham";
}
