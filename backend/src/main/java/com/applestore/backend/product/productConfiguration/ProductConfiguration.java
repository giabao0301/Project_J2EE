package com.applestore.backend.product.productConfiguration;

import com.applestore.backend.product.productItem.ProductItem;
import com.applestore.backend.product.variationOption.VariationOption;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_configuration")
public class ProductConfiguration {

    @EmbeddedId
    private ProductConfigurationId id;

    @ManyToOne
    @MapsId("productItemId")
    @JoinColumn(name = "product_item_id")
    private ProductItem productItem;

    @ManyToOne
    @MapsId("variationOptionId")
    @JoinColumn(name = "variation_option_id")
    private VariationOption variationOption;

    public ProductConfiguration(ProductItem productItem, VariationOption variationOption) {
        this.productItem = productItem;
        this.variationOption = variationOption;
    }
}
