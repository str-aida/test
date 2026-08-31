import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { TokenService } from '../services/token.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const tokenService = inject(TokenService);

  const token = tokenService.getToken();

  // Si no hay token, enviar la petición normal
  if (!token) {
    return next(req);
  }

  // Clonar la petición agregando Authorization
  const authReq = req.clone({

    setHeaders: {
      Authorization: `Bearer ${token}`
    }

  });

  return next(authReq);

};