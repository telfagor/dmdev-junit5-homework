package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;

    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;

    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;

    @Mock
    private Clock clock;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void upsert() {
        CreateSubscriptionDto dto = getDto();
        Subscription subscriptionExpectedResult = getSubscription();

        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(dto);
        doReturn(List.of(subscriptionExpectedResult)).when(subscriptionDao).findByUserId(subscriptionExpectedResult.getUserId());
        doReturn(subscriptionExpectedResult).when(createSubscriptionMapper).map(dto);
        doReturn(subscriptionExpectedResult).when(subscriptionDao).upsert(subscriptionExpectedResult);

        Subscription actualResult = subscriptionService.upsert(dto);

        verify(createSubscriptionValidator).validate(dto);
        verify(subscriptionDao).findByUserId(dto.getUserId());
        verify(createSubscriptionMapper).map(dto);
        verify(subscriptionDao).upsert(subscriptionExpectedResult);

        assertEquals(subscriptionExpectedResult, actualResult);
    }



    @Test
    void cancel() {
        Subscription subscription = getSubscription();

        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());
        doReturn(subscription).when(subscriptionDao).update(subscription);

        subscriptionService.cancel(subscription.getId());

        assertEquals(Status.CANCELED, subscription.getStatus());

        verify(subscriptionDao).findById(subscription.getId());
        verify(subscriptionDao).update(subscription);
    }

    @Test
    void expire() {
        Instant utcTime = Instant.now();
        Subscription subscription = getSubscription();

        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());
        doReturn(subscription).when(subscriptionDao).update(subscription);
        doReturn(utcTime).when(clock).instant();

        subscriptionService.expire(subscription.getId());

        assertEquals(Status.EXPIRED, subscription.getStatus());
        assertEquals(utcTime, subscription.getExpirationDate());

        verify(subscriptionDao).findById(subscription.getId());
        verify(subscriptionDao).update(subscription);
    }

    private Subscription getSubscription() {
        return Subscription.builder()
                .id(1)
                .userId(54)
                .name("Radu")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();
    }

    private CreateSubscriptionDto getDto() {
        return CreateSubscriptionDto.builder()
                .userId(54)
                .name("Andrei")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();
    }
}

