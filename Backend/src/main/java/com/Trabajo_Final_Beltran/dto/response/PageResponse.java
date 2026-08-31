package com.Trabajo_Final_Beltran.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {

  private List<T> content;

  private int pagina;

  private int size;

  private long totalElementos;

  private int totalPaginas;

  private boolean primera;

  private boolean ultima;
}