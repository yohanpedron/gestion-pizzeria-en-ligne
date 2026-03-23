package com.accenture.service;

import com.accenture.mapper.OrderMapper;
import com.accenture.model.Order;
import com.accenture.repository.OrderDao;
import lombok.AllArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;
    private final OrderMapper orderMapper;
    private final MessageSourceAccessor messages;

}
