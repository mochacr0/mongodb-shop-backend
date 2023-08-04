package com.example.springbootmongodb.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ShopAddress extends AbstractData {
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String hamlet;
    private String street;
    private String address;
    private boolean isDefault;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopAddress that = (ShopAddress) o;
        return isDefault() == that.isDefault()
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getPhoneNumber(), that.getPhoneNumber())
                && Objects.equals(getProvince(), that.getProvince())
                && Objects.equals(getDistrict(), that.getDistrict())
                && Objects.equals(getWard(), that.getWard())
                && Objects.equals(getHamlet(), that.getHamlet())
                && Objects.equals(getStreet(), that.getStreet())
                && Objects.equals(getAddress(), that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPhoneNumber(), getProvince(), getDistrict(), getWard(), getHamlet(), getStreet(), getAddress(), isDefault());
    }
}
