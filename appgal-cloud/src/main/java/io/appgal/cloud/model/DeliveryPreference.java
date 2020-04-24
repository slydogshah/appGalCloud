package io.appgal.cloud.model;

import java.util.List;

public class DeliveryPreference {
    private List<FoodTypes> foodTypes;

    public DeliveryPreference() {
    }

    public DeliveryPreference(List<FoodTypes> foodTypes) {
        this.foodTypes = foodTypes;
    }

    public List<FoodTypes> getFoodTypes() {
        return foodTypes;
    }

    public void setFoodTypes(List<FoodTypes> foodTypes) {
        this.foodTypes = foodTypes;
    }
}
