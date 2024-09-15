package com.dmdev.dao;

import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DaoTest {

    @Mock
    private SubscriptionDao subscriptionDao;

    @Test
    void checkUpsertForInsert() {
        Subscription subscription = new Subscription();

        doReturn(subscription).when(subscriptionDao).insert(subscription);

        subscriptionDao.insert(subscription);

        verify(subscriptionDao).insert(subscription);
        verify(subscriptionDao, never()).update(subscription);
    }

    @Test
    void checkUpsertForUpdate() {
        Subscription subscription = new Subscription();
        subscription.setId(1);

        doReturn(subscription).when(subscriptionDao).update(subscription);

        subscriptionDao.update(subscription);

        verify(subscriptionDao).update(subscription);
        verify(subscriptionDao, never()).insert(subscription);
    }
}