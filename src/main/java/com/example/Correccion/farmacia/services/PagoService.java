        package com.example.Correccion.farmacia.services;

        import com.example.Correccion.farmacia.dto.ProductoCarritoDTO;
        import com.example.Correccion.farmacia.dto.ProductoDTO;
        import com.stripe.Stripe;
        import com.stripe.exception.StripeException;
        import com.stripe.param.checkout.SessionCreateParams;
        import jakarta.annotation.PostConstruct;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.stereotype.Service;
        import com.stripe.model.checkout.Session;
        import java.util.ArrayList;
        import java.util.List;

        @Service
        public class PagoService {

            @Value("${stripe.secret.key}")
            private String stripeSecretKey;

            @PostConstruct
            public void init() {
                Stripe.apiKey = stripeSecretKey;
            }

            public Session crearSesionStripe(List<ProductoCarritoDTO> productos) throws Exception {
                List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

                for (ProductoCarritoDTO producto : productos) {
                    lineItems.add(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity((long) producto.getCantidad())
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount((long) (producto.getPrecio() * 100)) // en centavos
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(producto.getNombre())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    );
                    System.out.println("");
                }

                SessionCreateParams params = SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8080/pago-exitoso")
                        .setCancelUrl("http://localhost:8080/pago-cancelado")
                        .addAllLineItem(lineItems)
                        .build();

                return Session.create(params);
            }

        }
