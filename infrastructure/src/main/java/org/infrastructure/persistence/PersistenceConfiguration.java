package org.infrastructure.persistence;

import accounts.User;
import cart.Cart;
import catalog.Category;
import content.Page;
import discovery.Review;
import orders.Order;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = {
    User.class, Cart.class, Category.class, Order.class, Review.class, Page.class

})
@EnableJpaRepositories(basePackageClasses = PersistenceConfiguration.class)
public class PersistenceConfiguration {
}

