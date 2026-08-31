import { HttpInterceptorFn, HttpErrorResponse, HttpResponse } from "@angular/common/http";
import { NotificationService } from "../services/notification.service";
import { inject } from "@angular/core";
import { catchError, throwError, tap } from "rxjs";

export const notificationInterceptor: HttpInterceptorFn = (req, next) => {

  const notificationService = inject(NotificationService);

  return next(req).pipe(

    tap(event => {

      if (!(event instanceof HttpResponse)) {
        return;
      }

      const method = req.method.toUpperCase();

      const shouldNotifySuccess =
        ['POST', 'PUT', 'PATCH', 'DELETE'].includes(method);

      if (!shouldNotifySuccess) {
        return;
      }

      const message = getSuccessMessage(event.body);

      if (message) {
        notificationService.success(message);
      }

    }),

    catchError((error: HttpErrorResponse) => {

      const message = getErrorMessage(error);

      notificationService.error(message);

      return throwError(() => error);

    })

  );
};


function getSuccessMessage(body: unknown): string | null {

  if (typeof body === 'string' && body.trim()) {
    
    try {
      const parsed = JSON.parse(body);

      if (
        parsed &&
        typeof parsed === 'object' &&
        'message' in parsed &&
        typeof parsed.message === 'string'
      ) {
        return parsed.message;
      }

      if (
        parsed &&
        typeof parsed === 'object' &&
        'mensaje' in parsed &&
        typeof parsed.mensaje === 'string'
      ) {
        return parsed.mensaje;
      }

    } catch {
      // Es texto plano.
    }

    return body;

  }

  if (
    body &&
    typeof body === 'object' &&
    'mensaje' in body &&
    typeof body.mensaje === 'string'
  ) {
    return body.mensaje;
  }

  if (
    body &&
    typeof body === 'object' &&
    'message' in body &&
    typeof body.message === 'string'
  ) {
    return body.message;
  }

  return null;
}

function getErrorMessage(error: HttpErrorResponse): string {

  const body = error.error;

  // Si el backend devuelve texto
  if (typeof body === 'string' && body.trim()) {
    
    try {
      const parsed = JSON.parse(body);

      if (
        parsed &&
        typeof parsed === 'object' &&
        'message' in parsed &&
        typeof parsed.message === 'string'
      ) {
        return parsed.message;
      }

      if (
        parsed &&
        typeof parsed === 'object' &&
        'mensaje' in parsed &&
        typeof parsed.mensaje === 'string'
      ) {
        return parsed.mensaje;
      }

    } catch {
      // No era JSON, entonces realmente era texto plano.
    }

    return body;

  }

  // Si Angular ya recibió un objeto JSON
  if (
    body &&
    typeof body === 'object' &&
    'mensaje' in body &&
    typeof body.mensaje === 'string'
  ) {
    return body.mensaje;
  }

  if (
    body &&
    typeof body === 'object' &&
    'message' in body &&
    typeof body.message === 'string'
  ) {
    return body.message;
  }

  return getDefaultErrorMessage(error.status);
}


function getDefaultErrorMessage(status: number): string {

  switch (status) {

    case 400:
      return 'La solicitud no es válida.';

    case 401:
      return 'La sesión no es válida o ha expirado.';

    case 403:
      return 'No tenés permisos para realizar esta acción.';

    case 404:
      return 'No se encontró el recurso solicitado.';

    case 409:
      return 'La operación no se puede realizar porque existe un conflicto.';

    case 500:
      return 'Ocurrió un error interno en el servidor.';

    default:
      return 'Ocurrió un error inesperado.';
  }
}