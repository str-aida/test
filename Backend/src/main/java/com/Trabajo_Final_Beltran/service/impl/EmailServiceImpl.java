package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import com.Trabajo_Final_Beltran.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailSenderServiceImpl emailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    @Override
    public void enviarEmail(String destino, String asunto, String cuerpo) {
        emailSender.enviarEmail(destino, asunto, cuerpo);
    }

    @Override
    public void enviarEmailRecuperacion(String destino, String codigo, Establecimiento establecimiento) {
        Context context = new Context();
        context.setVariable("codigo", codigo);
        context.setVariable("email", destino);
        context.setVariable("nombreEstablecimiento", establecimiento.getNombre());

        String html = templateEngine.process("email/recuperacion", context);
        emailSender.enviarEmail(destino, "Código de recuperación de contraseña", html);
    }

    @Override
    public void enviarEmailCupon(String destino, String nombre, Cupon cupon, Establecimiento establecimiento,
            boolean asignacionManual) {
        String descuentoTexto = cupon.getTipoDescuento() == TipoDescuento.PORCENTAJE
                ? cupon.getValor() + "% de descuento"
                : "$" + cupon.getValor() + " de descuento";

        Context context = new Context();
        context.setVariable("nombre", nombre);
        context.setVariable("descuentoTexto", descuentoTexto);
        context.setVariable("codigo", cupon.getCodigo());
        context.setVariable("fechaFin", cupon.getFechaFin());
        context.setVariable("esAutomatico", !asignacionManual);
        context.setVariable("nombreEstablecimiento", establecimiento.getNombre());

        String html = templateEngine.process("email/cupon", context);
        emailSender.enviarEmail(destino, "¡Tenés un cupón esperándote!", html);
    }

    @Override
    public void enviarEmailPedidoListo(String destino, String numeroPedido, String nombre,
            Establecimiento establecimiento) {
        Context context = new Context();
        context.setVariable("nombre", nombre);
        context.setVariable("numeroPedido", numeroPedido);
        context.setVariable("nombreEstablecimiento", establecimiento.getNombre());

        String html = templateEngine.process("email/pedido-listo", context);
        emailSender.enviarEmail(destino, "Tu pedido está listo", html);
    }
}