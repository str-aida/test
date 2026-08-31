package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.LogSistemaResponse;
import com.Trabajo_Final_Beltran.exception.PdfException;
import com.Trabajo_Final_Beltran.service.LogSistemaPdfService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.font.PDFont;

@Service
public class LogSistemaPdfServiceImpl implements LogSistemaPdfService {

  private static final float MARGEN = 30;

  private static final float ANCHO_PAGINA =
      PDRectangle.A4.getHeight();


  private static final float ANCHO_UTIL =
      ANCHO_PAGINA - (MARGEN * 2);

  private static final float ESPACIO_SEGURIDAD = 20;

  private static final float ANCHO_TABLA =
      ANCHO_UTIL - ESPACIO_SEGURIDAD;

  private static final float ANCHO_COLUMNAS_ORIGINAL = 782;

  private static final float FACTOR_ESCALA =
      ANCHO_TABLA / ANCHO_COLUMNAS_ORIGINAL;

  private static final float ALTURA_FILA = 20;

  private static final float TAMANIO_FUENTE = 6;

  private static final float LIMITE_INFERIOR = 40;

  private static final float ANCHO_FECHA =
      55 * FACTOR_ESCALA;

  private static final float ANCHO_USUARIO =
      70 * FACTOR_ESCALA;

  private static final float ANCHO_ROL =
      45 * FACTOR_ESCALA;

  private static final float ANCHO_ACCION =
      60 * FACTOR_ESCALA;

  private static final float ANCHO_TABLA_COLUMNA =
      60 * FACTOR_ESCALA;

  private static final float ANCHO_REFERENCIA =
      70 * FACTOR_ESCALA;

  private static final float ANCHO_CAMPO =
      60 * FACTOR_ESCALA;

  private static final float ANCHO_VALOR_ANTERIOR =
      85 * FACTOR_ESCALA;

  private static final float ANCHO_VALOR_NUEVO =
      85 * FACTOR_ESCALA;

  private static final float ANCHO_DESCRIPCION =
      192 * FACTOR_ESCALA;

  private static final DateTimeFormatter FORMATO_FECHA =
      DateTimeFormatter.ofPattern(
          "dd/MM/yyyy HH:mm"
      );


  @Override
  public byte[] generarPdf(
      List<LogSistemaResponse> logs
  ) {

    try (
        PDDocument document =
            new PDDocument();

        ByteArrayOutputStream outputStream =
            new ByteArrayOutputStream()
    ) {

      PDPage page =
          new PDPage(
              new PDRectangle(
                  PDRectangle.A4.getHeight(),
                  PDRectangle.A4.getWidth()
              )
          );

      document.addPage(page);

      PDPageContentStream contentStream =
          new PDPageContentStream(
              document,
              page
          );

      try {

        contentStream.beginText();

        contentStream.setFont(
            new PDType1Font(
                Standard14Fonts.FontName.HELVETICA_BOLD
            ),
            18
        );

        contentStream.newLineAtOffset(
            50,
            550
        );

        contentStream.showText(
            "Reporte de Auditoria"
        );

        contentStream.setFont(
            new PDType1Font(
                Standard14Fonts.FontName.HELVETICA
            ),
            10
        );

        contentStream.newLineAtOffset(
            0,
            -25
        );

        contentStream.showText(
            "Fecha de generacion: "
                + LocalDateTime.now().format(
                FORMATO_FECHA
            )
        );

        contentStream.newLineAtOffset(
            0,
            -15
        );

        contentStream.showText(
            "Cantidad de registros: "
                + logs.size()
        );

        contentStream.endText();

        dibujarEncabezadoTabla(
            contentStream,
            470
        );

        float posicionY = 450;

        for (LogSistemaResponse log : logs) {

          if (
              posicionY - ALTURA_FILA
                  < LIMITE_INFERIOR
          ) {

            contentStream.close();

            page =
                new PDPage(
                    new PDRectangle(
                        PDRectangle.A4.getHeight(),
                        PDRectangle.A4.getWidth()
                    )
                );

            document.addPage(page);

            contentStream =
                new PDPageContentStream(
                    document,
                    page
                );

            dibujarEncabezadoTabla(
                contentStream,
                550
            );

            posicionY = 530;
          }

          dibujarFila(
              contentStream,
              log,
              posicionY
          );

          posicionY -= ALTURA_FILA;
        }

      } finally {

        if (contentStream != null) {
          contentStream.close();
        }
      }

      document.save(outputStream);

      return outputStream.toByteArray();

    } catch (IOException e) {

      throw new PdfException(
          "Error al generar el PDF de auditoría",
          e
      );
    }
  }

  private void dibujarEncabezadoTabla(
      PDPageContentStream contentStream,
      float posicionY
  ) throws IOException {

    String[] encabezados = {
        "Fecha",
        "Usuario",
        "Rol",
        "Acción",
        "Tabla",
        "Referencia",
        "Campo",
        "Valor anterior",
        "Valor nuevo",
        "Descripción"
    };

    float[] anchos = {
        ANCHO_FECHA,
        ANCHO_USUARIO,
        ANCHO_ROL,
        ANCHO_ACCION,
        ANCHO_TABLA_COLUMNA,
        ANCHO_REFERENCIA,
        ANCHO_CAMPO,
        ANCHO_VALOR_ANTERIOR,
        ANCHO_VALOR_NUEVO,
        ANCHO_DESCRIPCION
    };

    float posicionX = MARGEN;

    contentStream.setFont(
        new PDType1Font(
            Standard14Fonts.FontName.HELVETICA_BOLD
        ),
        TAMANIO_FUENTE
    );

    contentStream.beginText();

    contentStream.newLineAtOffset(
        posicionX,
        posicionY
    );

    for (int i = 0; i < encabezados.length; i++) {

      contentStream.showText(
          encabezados[i]
      );

      contentStream.newLineAtOffset(
          anchos[i],
          0
      );
    }

    contentStream.endText();
  }

  private String[] obtenerValoresFila(
      LogSistemaResponse log
  ) {

    return new String[] {
        log.getFecha() != null
            ? log.getFecha().format(FORMATO_FECHA)
            : "",
        log.getUsuario() != null
            ? log.getUsuario()
            : "",
        log.getRol() != null
            ? log.getRol().name()
            : "",
        log.getAccion() != null
            ? log.getAccion()
            : "",
        log.getTablaAfectada() != null
            ? log.getTablaAfectada()
            : "",
        log.getReferencia() != null
            ? log.getReferencia()
            : "",
        log.getCampoModificado() != null
            ? log.getCampoModificado()
            : "",
        log.getValorAnterior() != null
            ? log.getValorAnterior()
            : "",
        log.getValorNuevo() != null
            ? log.getValorNuevo()
            : "",
        log.getDescripcion() != null
            ? log.getDescripcion()
            : ""
    };
  }

  private void dibujarFila(
      PDPageContentStream contentStream,
      LogSistemaResponse log,
      float posicionY
  ) throws IOException {

    String[] valores =
        obtenerValoresFila(log);

    float[] anchos = {
        ANCHO_FECHA,
        ANCHO_USUARIO,
        ANCHO_ROL,
        ANCHO_ACCION,
        ANCHO_TABLA_COLUMNA,
        ANCHO_REFERENCIA,
        ANCHO_CAMPO,
        ANCHO_VALOR_ANTERIOR,
        ANCHO_VALOR_NUEVO,
        ANCHO_DESCRIPCION
    };

    PDFont fuente =
        new PDType1Font(
            Standard14Fonts.FontName.HELVETICA
        );

    float posicionX = MARGEN;

    contentStream.setFont(
        fuente,
        TAMANIO_FUENTE
    );

    contentStream.beginText();

    contentStream.newLineAtOffset(
        posicionX,
        posicionY
    );

    for (int i = 0; i < valores.length; i++) {

      String valorAjustado =
          ajustarTexto(
              valores[i],
              anchos[i],
              fuente,
              TAMANIO_FUENTE
          );

      contentStream.showText(
          valorAjustado
      );

      contentStream.newLineAtOffset(
          anchos[i],
          0
      );
    }

    contentStream.endText();
  }

  private String ajustarTexto(
      String texto,
      float anchoMaximo,
      PDFont fuente,
      float tamanioFuente
  ) throws IOException {

    if (texto == null || texto.isBlank()) {
      return "";
    }

    float anchoTexto =
        fuente.getStringWidth(
            texto
        ) / 1000
            * tamanioFuente;

    if (anchoTexto <= anchoMaximo) {
      return texto;
    }

    String sufijo = "...";

    StringBuilder resultado =
        new StringBuilder();

    for (char caracter : texto.toCharArray()) {

      String textoPrueba =
          resultado.toString()
              + caracter
              + sufijo;

      float anchoPrueba =
          fuente.getStringWidth(
              textoPrueba
          ) / 1000
              * tamanioFuente;

      if (anchoPrueba > anchoMaximo) {
        break;
      }

      resultado.append(
          caracter
      );
    }

    return resultado
        + sufijo;
  }
}