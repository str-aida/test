import { Component, ElementRef, HostListener, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { 
  LucideBell, 
  LucideShoppingBag, 
  LucideTag, 
  LucideCreditCard, 
  LucideInfo,
  LucideInbox
} from '@lucide/angular';
import { NotificacionesService } from '../../../../core/services/notificaciones.service';
import { NotificacionNavigationService } from '../../../../core/services/notificacion-navigation.service';
import { NotificacionResponse } from '../../../../core/models/notificacion-response';
import { TipoReferencia } from '../../../../core/models/enums/tipo-referencia.enum';
import { TipoNotificacion } from '../../../../core/models/enums/tipo-notificacion.enum';

@Component({
  selector: 'app-notificaciones-bell',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    LucideBell,
    LucideShoppingBag,
    LucideTag,
    LucideCreditCard,
    LucideInfo,
    LucideInbox
  ],
  templateUrl: './notificaciones-bell.html',
  styleUrl: './notificaciones-bell.scss'
})
export class NotificacionesBellComponent implements OnInit {

  private readonly notificacionesService = inject(NotificacionesService);
  private readonly navigationService = inject(NotificacionNavigationService);
  private readonly elementRef = inject(ElementRef);

  readonly unreadCount = signal<number>(0);
  readonly notificaciones = signal<NotificacionResponse[]>([]);
  readonly isOpen = signal<boolean>(false);
  readonly isLoading = signal<boolean>(false);

  readonly TipoNotificacion = TipoNotificacion;
  readonly TipoReferencia = TipoReferencia;

  ngOnInit(): void {
    this.cargarConteoNoLeidas();
  }

  cargarConteoNoLeidas(): void {
    this.notificacionesService.contarNoLeidas().subscribe({
      next: (count) => {
        this.unreadCount.set(count);
      },
      error: (err) => {
        console.error('Error al cargar conteo de notificaciones no leídas:', err);
      }
    });
  }

  toggleDropdown(): void {
    const nextState = !this.isOpen();
    this.isOpen.set(nextState);
    if (nextState) {
      this.cargarNotificaciones();
    }
  }

  cargarNotificaciones(): void {
    this.isLoading.set(true);
    this.notificacionesService.obtenerMisNotificaciones().subscribe({
      next: (data) => {
        this.notificaciones.set(data ?? []);
        this.isLoading.set(false);
        // Actualizar el conteo local de no leídas basado en los datos recibidos
        const unreadInList = (data ?? []).filter(n => !n.leida).length;
        if (data.length < 10) {
          this.unreadCount.set(unreadInList);
        }
      },
      error: (err) => {
        console.error('Error al obtener notificaciones:', err);
        this.isLoading.set(false);
      }
    });
  }

  onNotificationClick(notificacion: NotificacionResponse, event?: MouseEvent): void {
    if (event) {
      event.stopPropagation();
    }

    console.log(`[NotificationClick] Click recibido en Notificación ID: ${notificacion.id}, Título: "${notificacion.titulo}", Leída: ${notificacion.leida}, TipoRef: ${notificacion.tipoReferencia}, RefId: ${notificacion.referenciaId}`);

    // 1. Si no está leída, marcar como leída localmente y enviar PATCH al backend
    if (!notificacion.leida) {
      // Actualización inmediata de estado local para UX fluida
      this.notificaciones.update(list =>
        list.map(item => item.id === notificacion.id ? { ...item, leida: true } : item)
      );
      this.unreadCount.update(c => Math.max(0, c - 1));

      // Solicitud al backend en segundo plano
      this.notificacionesService.marcarComoLeida(notificacion.id).subscribe({
        error: (err) => {
          console.error('Error al marcar notificación como leída en backend:', err);
        }
      });
    }

    // 2. Cerrar desplegable
    this.isOpen.set(false);

    // 3. Ejecutar resolución y navegación dinámica centralizada
    this.navigationService.navegar(notificacion);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (this.isOpen() && !this.elementRef.nativeElement.contains(event.target as Node)) {
      this.isOpen.set(false);
    }
  }

  @HostListener('document:keydown.escape')
  onEscapeKey(): void {
    this.isOpen.set(false);
  }

}
