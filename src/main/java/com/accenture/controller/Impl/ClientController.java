package com.accenture.controller.Impl;

import com.accenture.controller.ClientApi;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ClientController implements ClientApi {

    private final ClientService clientService;


}
