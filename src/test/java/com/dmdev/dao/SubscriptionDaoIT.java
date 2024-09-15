package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void insert() {
        Subscription subscription = getSubscription(1);

        Subscription actualResult = subscriptionDao.insert(subscription);

        assertNotNull(actualResult.getId());
    }

    @Test
    void update() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1));
        subscription.setName("Igor");
        subscription.setProvider(Provider.APPLE);

        Subscription updatedSubscription = subscriptionDao.update(subscription);
        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());
        
        assertThat(actualResult).contains(updatedSubscription);
    }

    @Test
    void findAll() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription(1));
        Subscription subscription2 = subscriptionDao.insert(getSubscription(2));
        Subscription subscription3 = subscriptionDao.insert(getSubscription(3));

        List<Subscription> subscriptions = subscriptionDao.findAll();

        assertThat(subscriptions).hasSize(3);
        List<Integer> subscriptionIds = subscriptions.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(subscriptionIds).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1));

        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getId()).isEqualTo(subscription.getId());
    }

    @Test
    void findByUserId() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1));

        List<Subscription> subscriptions = subscriptionDao.findByUserId(subscription.getUserId());

        assertThat(subscriptions).hasSize(1);
        assertThat(subscriptions.get(0).getUserId()).isEqualTo(subscription.getUserId());
    }

    @Test
    void deleteExistingEntity() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1));

        boolean actualResult = subscriptionDao.delete(subscription.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExistingEntity() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1));

        boolean actualResult = subscriptionDao.delete(-1);

        assertFalse(actualResult);
    }

    private Subscription getSubscription(Integer userId) {
        return Subscription.builder()
                .userId(userId)
                .name("Andrei")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .status(Status.ACTIVE)
                .build();
    }
}
