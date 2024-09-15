package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();
    private final CreateSubscriptionMapper createSubscriptionMapper = CreateSubscriptionMapper.getInstance();
    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();
    private final Clock clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));
    private final SubscriptionService subscriptionService = new SubscriptionService(
            subscriptionDao, createSubscriptionMapper, validator, clock
    );

    @Test
    void shouldInsertIfIdIsNull() {
        CreateSubscriptionDto createSubscriptionDto = getSubscriptionDto(1, "Andrei");

        Subscription actualResult = subscriptionService.upsert(createSubscriptionDto);

        assertNotNull(actualResult.getId());
    }

    @Test
    void shouldUpdateIfIdIsNotNull() {
        Subscription subscription = subscriptionDao.insert(getSubscription());
        CreateSubscriptionDto createSubscriptionDto = getSubscriptionDto(subscription.getUserId(), subscription.getName());

        Subscription actualResult = subscriptionService.upsert(createSubscriptionDto);

        assertEquals(actualResult.getExpirationDate(), createSubscriptionDto.getExpirationDate());
        assertEquals(Status.ACTIVE, actualResult.getStatus());
    }


    @Test
    void shouldThrowExceptionIfSubscriptionStatusIsNotActive() {
        Subscription subscription = Subscription.builder()
                .userId(1)
                .name("Andrei")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
                .status(Status.CANCELED)
                .build();

        Subscription createdSubscription = subscriptionDao.insert(subscription);

        assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(createdSubscription.getId()));
    }

    private Subscription getSubscription() {
        return Subscription.builder()
                .userId(1)
                .name("Andrei")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();
    }

    private CreateSubscriptionDto getSubscriptionDto(Integer userId, String name) {
        return CreateSubscriptionDto.builder()
                .userId(userId)
                .name(name)
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now().plus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
                .build();
    }
}