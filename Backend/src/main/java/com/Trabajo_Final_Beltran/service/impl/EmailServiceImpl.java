package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.enums.TipoDescuento;
import com.Trabajo_Final_Beltran.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailSenderServiceImpl emailSender;

    @Override
    public void enviarEmail(String destino, String asunto, String cuerpo) {
        emailSender.enviarEmail(destino, asunto, cuerpo);
    }

    @Override
    public void enviarEmailRecuperacion(String destino, String codigo) {
        String asunto = "Código de recuperación de contraseña";
        String cuerpo =
                "Hola,\n\n"
                + "Recibimos una solicitud para restablecer la contraseña de tu cuenta en Gestia.\n\n"
                + "Tu código de verificación es:\n\n"
                + "        " + codigo + "\n\n"
                + "Ingresá este código en la pantalla de recuperación de contraseña para continuar.\n\n"
                + "Este código es válido por 15 minutos. Si no solicitaste este cambio, "
                + "podés ignorar este correo — tu contraseña seguirá siendo la misma.\n\n"
                + "Por tu seguridad, nunca compartas este código con nadie, ni siquiera "
                + "con alguien que diga representar a nuestro equipo.\n\n"
                + "Saludos,\n"
                + "El equipo de Gestia";
        emailSender.enviarEmail(destino, asunto, cuerpo);
    }

    @Override
    public void enviarEmailCupon(String destino, String nombre, Cupon cupon) {
        String asunto = "¡Tenés un cupón esperándote!";

        String descuento = cupon.getTipoDescuento() == TipoDescuento.PORCENTAJE
                ? cupon.getValor() + "% de descuento"
                : "$" + cupon.getValor() + " de descuento";

        String cuerpo =
                "Hola " + nombre + ",\n\n"
                + "¡Tenemos una sorpresa para vos! Te asignamos un cupón de "
                + descuento + " para tu próxima compra.\n\n"
                + "Código: " + cupon.getCodigo() + "\n"
                + "Válido hasta: " + cupon.getFechaFin() + "\n\n"
                + "Ingresá el código al finalizar tu pedido para aplicar el descuento.\n\n"
                + "¡Te esperamos!\n\n"
                + "El equipo de Gestia";

        emailSender.enviarEmail(destino, asunto, cuerpo);
    }
    
    
    
    @Override
    public void enviarEmailPedidoListo(String destino, String numeroPedido , String nombre) {

        String asunto =
            "Tu pedido está listo";

        String cuerpo =
            "Hola "+ nombre+"\n\n"
                + "Tu pedido "
                + numeroPedido
                + " ya está listo para retirar.\n\n"
                + "¡Te esperamos!\n\n"
                + "Gracias por elegirnos.";

        emailSender.enviarEmail(
            destino,
            asunto,
            cuerpo
        );
      }

    }