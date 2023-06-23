package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.UserAddress;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import static com.example.springbootmongodb.model.ModelConstants.USER_ADDRESS_COLLECTION_NAME;

@Document(value = USER_ADDRESS_COLLECTION_NAME)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserAddressEntity extends AbstractEntity {
    @Field(targetType = FieldType.OBJECT_ID)
    private String userId;
    private String name;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String streetAddress;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserAddressEntity [id=");
        builder.append(this.getId());
        builder.append(", userId=");
        builder.append(this.getUserId());
        builder.append(", name=");
        builder.append(this.getName());
        builder.append(", phoneNumber=");
        builder.append(this.getPhoneNumber());
        builder.append(", province=");
        builder.append(this.getProvince());
        builder.append(", district=");
        builder.append(this.getDistrict());
        builder.append(", ward=");
        builder.append(this.getWard());
        builder.append(", streetAddress=");
        builder.append(this.getStreetAddress());
        return builder.toString();
    }
//    @Override
//    public UserAddress toData() {
//        UserAddress data = new UserAddress();
//        data.setId(this.getId());
//        data.setUserId(this.getUserId());
//        data.setName(this.getName());
//        data.setProvince(this.getProvince());
//        data.setDistrict(this.getDistrict());
//        data.setWard(this.getWard());
//        data.setStreetAddress(this.getStreetAddress());
//        data.setCreatedAt(this.getCreatedAt());
//        data.setUpdatedAt(this.getUpdatedAt());
//        return data;
//    }

    public void fromData(UserAddress data) {
        this.setName(data.getName());
        this.setProvince(data.getProvince());
        this.setDistrict(data.getDistrict());
        this.setWard(data.getWard());
        this.setStreetAddress(data.getStreetAddress());
    }
}
