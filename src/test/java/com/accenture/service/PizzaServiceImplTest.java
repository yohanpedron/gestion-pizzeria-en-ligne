package com.accenture.service;

import com.accenture.mapper.PizzaMapper;
import com.accenture.model.Ingredient;
import com.accenture.model.Pizza;
import com.accenture.model.PizzaSize;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PizzaServiceImplTest {

    @Mock
    private PizzaDao pizzaDao;

    @Mock
    private PizzaMapper pizzaMapper;

    @Mock
    private MessageSourceAccessor messages;

    @InjectMocks
    private PizzaServiceImpl pizzaService;

    @BeforeEach
    void setUp() {
        //MockitoExtension + InjectMocks s'occupent de tout
    }

    @Test
    @DisplayName("Test when Pizza is persisted in BDD")
    void testWhenPizzaIsPersistedInBDD() {
        // Je crée mes variables pour les tester
        PizzaServiceImpl spy = Mockito.spy(pizzaService);
        String name = "Margherita";
        Map<PizzaSize, Double> price = Map.of(PizzaSize.MEDIUM, 9.0);
        List<IngredientRequestDto> ingredientDtos = List.of(new IngredientRequestDto("Tomate"));

        // Je crée mon request comme s'il venait du controller
        PizzaRequestDto dtoRequest = new PizzaRequestDto(
                name,
                price,
                ingredientDtos,
                true
        );
        // Je crée mon entité (avant sauvegarde)
        Ingredient ingredientEntity = new Ingredient("Tomate", 10);
        Pizza pizza= new Pizza(
                name,
                price,
                List.of(ingredientEntity),
                true
        );

        // Je crée mon entité sauvegardée (avec ID)
        Ingredient savedIngredient = new Ingredient(UUID.randomUUID(), "Tomate", 10);
        Pizza savedPizza = new Pizza(
                UUID.randomUUID(),
                name,
                price,
                List.of(savedIngredient),
                true
        );
        // Je crée ma response souhaitée
        PizzaResponseDto returnedResponse = new PizzaResponseDto(
                savedPizza.getId(),
                savedPizza.getName(),
                savedPizza.getPrice(),
                List.of(new IngredientResponseDto(
                        savedIngredient.getId(),
                        savedIngredient.getName()
                )),
                true
        );

        // Je mock les méthodes spécifiques utilisées par la méthode que je veux tester
        Mockito.when(pizzaMapper.toPizza(Mockito.any(PizzaRequestDto.class))).thenReturn(pizza);
        Mockito.when(pizzaDao.save(Mockito.any(Pizza.class))).thenReturn(savedPizza);
        Mockito.when(pizzaMapper.toPizzaResponseDto(Mockito.any(Pizza.class))).thenReturn(returnedResponse);

        //j'appelle la méthode du service
        PizzaResponseDto returnedValue = spy.addPizza(dtoRequest);

        // Vérification des appels internes
        Mockito.verify(spy, Mockito.times(1)).addPizza(Mockito.any(PizzaRequestDto.class));
        Mockito.verify(pizzaMapper, Mockito.times(1)).toPizza(dtoRequest);
        Mockito.verify(pizzaDao, Mockito.times(1)).save(pizza);
        Mockito.verify(pizzaMapper, Mockito.times(1)).toPizzaResponseDto(savedPizza);
    }

}