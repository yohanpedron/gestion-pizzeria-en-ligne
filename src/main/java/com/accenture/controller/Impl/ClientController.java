package com.accenture.controller.Impl;

import com.accenture.controller.ClientApi;
import com.accenture.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ClientController implements ClientApi {

    private final ClientService clientService;


}
