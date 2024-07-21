package com.infuq.consumer.listener.mq.business;

import java.util.Properties;

public interface BusinessMQListener<T> {

    void handler(T t, Properties userProperties);
}