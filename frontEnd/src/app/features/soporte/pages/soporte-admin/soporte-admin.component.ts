import {
  Component,
  ElementRef,
  inject,
  OnDestroy,
  OnInit,
  signal,
  ViewChild,
  PLATFORM_ID
} from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import {
  LucideMessageSquare,
  LucideSend,
  LucideCheck,
  LucideCheckCheck,
  LucideLock,
  LucideSearch,
  LucideUser,
  LucideAlertCircle,
  LucideX,
  LucideRefreshCw,
  LucideSlidersHorizontal,
  LucideInbox,
  LucideArrowLeft
} from '@lucide/angular';
import { ChatService, SpringPage } from '../../../../core/services/chat.service';
import { ChatSocketService } from '../../../../core/services/chat-socket.service';
import { TokenService } from '../../../../core/services/token.service';
import {
  ChatConversacionResponse,
  ChatLecturaResponse,
  ChatMensajeResponse,
  EstadoConversacion,
  PedidoSnapshot
} from '../../../../core/models/chat.models';

@Component({
  selector: 'app-soporte-admin',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DatePipe,
    DecimalPipe,
    LucideMessageSquare,
    LucideSend,
    LucideCheck,
    LucideCheckCheck,
    LucideLock,
    LucideSearch,
    LucideUser,
    LucideAlertCircle,
    LucideX,
    LucideRefreshCw,
    LucideSlidersHorizontal,
    LucideInbox,
    LucideArrowLeft
  ],
  templateUrl: './soporte-admin.component.html',
  styleUrl: './soporte-admin.component.scss'
})
export class SoporteAdminComponent implements OnInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly chatService = inject(ChatService);
  private readonly chatSocketService = inject(ChatSocketService);
  private readonly tokenService = inject(TokenService);
  private readonly platformId = inject(PLATFORM_ID);

  @ViewChild('messagesContainer') private messagesContainer?: ElementRef<HTMLDivElement>;

  // Pestaña y filtros
  tabActiva = signal<EstadoConversacion>('ABIERTA');
  filtroTexto: string = '';

  // Datos de bandeja
  conversaciones = signal<ChatConversacionResponse[]>([]);
  totalAbiertas = signal<number>(0);
  totalEnAtencion = signal<number>(0);
  totalCerradas = signal<number>(0);
  isLoadingBandeja = signal<boolean>(true);

  // Conversación seleccionada y mensajes
  conversacionSeleccionada = signal<ChatConversacionResponse | null>(null);
  mensajes = signal<ChatMensajeResponse[]>([]);
  pedidoSnapshot = signal<PedidoSnapshot | null>(null);
  isLoadingChat = signal<boolean>(false);
  isLoadingMore = signal<boolean>(false);
  hasMorePages = signal<boolean>(false);
  currentPage = signal<number>(0);

  // Estados de acción y conectividad
  isAsignando = signal<number | null>(null);
  isClosing = signal<boolean>(false);
  errorMsg = signal<string | null>(null);
  conectado = signal<boolean>(false);

  // Input de chat
  nuevoMensajeTexto = '';

  private subs: Subscription[] = [];
  private activeConversacionId: number | null = null;

  ngOnInit(): void {
    // 1. Cargar conteos y bandeja inicial
    this.actualizarConteosGenerales();
    this.cargarBandeja();

    // 2. Revisar si la ruta trae un id preseleccionado
    const paramSub = this.route.paramMap.subscribe((params) => {
      const idParam = params.get('id');
      if (idParam && !isNaN(+idParam)) {
        this.abrirConversacionPorId(+idParam);
      }
    });
    this.subs.push(paramSub);

    // 3. Suscribirse a socket (conexión y eventos)
    const connSub = this.chatSocketService.conectado.subscribe((connected) => {
      this.conectado.set(connected);
    });
    this.subs.push(connSub);

    const msgSub = this.chatSocketService.mensajes$.subscribe((mensaje) => {
      this.procesarMensajeRecibido(mensaje);
    });
    this.subs.push(msgSub);

    const lectSub = this.chatSocketService.lecturas$.subscribe((lectura) => {
      this.procesarLecturaRecibida(lectura);
    });
    this.subs.push(lectSub);

    const estadoSub = this.chatSocketService.estado$.subscribe((estadoActualizado) => {
      this.procesarCambioEstado(estadoActualizado);
    });
    this.subs.push(estadoSub);

    const errSub = this.chatSocketService.errores$.subscribe((err) => {
      this.errorMsg.set(err.mensaje);
      setTimeout(() => this.errorMsg.set(null), 6000);
    });
    this.subs.push(errSub);

    const reconnSub = this.chatSocketService.reconectado.subscribe(() => {
      if (this.activeConversacionId) {
        this.recargarMensajesRecientes(this.activeConversacionId);
      }
    });
    this.subs.push(reconnSub);
  }

  ngOnDestroy(): void {
    this.subs.forEach((s) => s.unsubscribe());
    this.chatSocketService.desconectar();
  }

  cambiarTab(tab: EstadoConversacion): void {
    if (this.tabActiva() === tab) return;
    this.tabActiva.set(tab);
    this.cargarBandeja();
  }

  cargarBandeja(): void {
    this.isLoadingBandeja.set(true);
    this.chatService.listar(this.tabActiva(), 0, 50).subscribe({
      next: (page: SpringPage<ChatConversacionResponse>) => {
        this.conversaciones.set(page.content || []);
        this.isLoadingBandeja.set(false);
      },
      error: (err) => {
        console.error('Error al cargar bandeja de chats:', err);
        this.isLoadingBandeja.set(false);
        const msg = err.error?.mensaje || err.error?.message || 'No se pudo cargar la bandeja de soporte.';
        this.errorMsg.set(msg);
      }
    });
  }

  actualizarConteosGenerales(): void {
    this.chatService.listar('ABIERTA', 0, 1).subscribe({
      next: (res) => this.totalAbiertas.set(res.totalElements ?? res.content.length),
      error: () => {}
    });
    this.chatService.listar('EN_ATENCION', 0, 1).subscribe({
      next: (res) => this.totalEnAtencion.set(res.totalElements ?? res.content.length),
      error: () => {}
    });
    this.chatService.listar('CERRADA', 0, 1).subscribe({
      next: (res) => this.totalCerradas.set(res.totalElements ?? res.content.length),
      error: () => {}
    });
  }

  seleccionarConversacion(c: ChatConversacionResponse): void {
    if (this.activeConversacionId === c.id) return;
    this.abrirConversacionPorId(c.id);
  }

  private abrirConversacionPorId(id: number): void {
    this.activeConversacionId = id;
    this.isLoadingChat.set(true);
    this.mensajes.set([]);
    this.currentPage.set(0);
    this.hasMorePages.set(false);
    this.pedidoSnapshot.set(null);

    this.chatService.obtenerDetalle(id).subscribe({
      next: (conv) => {
        this.conversacionSeleccionada.set(conv);
        // Conectar al socket de este chat
        this.chatSocketService.conectar(id);
        // Cargar primera página de mensajes (orden DESC)
        this.cargarMensajesIniciales(id);
      },
      error: (err) => {
        console.error('Error al cargar detalle del chat:', err);
        this.isLoadingChat.set(false);
        const msg = err.error?.mensaje || err.error?.message || 'No se pudo cargar la conversación.';
        this.errorMsg.set(msg);
      }
    });
  }

  private cargarMensajesIniciales(id: number): void {
    this.chatService.obtenerMensajes(id, 0, 20).subscribe({
      next: (page: SpringPage<ChatMensajeResponse>) => {
        // En backend vienen DESC (index 0 es el más reciente). Invertimos para mostrar cronológico (arriba viejos, abajo nuevos)
        const cronologicos = [...(page.content || [])].reverse();
        this.mensajes.set(cronologicos);
        this.hasMorePages.set(!page.last && (page.content?.length ?? 0) > 0);
        this.currentPage.set(0);

        this.extraerSnapshotDeMensajes(cronologicos);

        this.isLoadingChat.set(false);
        this.scrollHaciaAbajo(true);
      },
      error: (err) => {
        console.error('Error al cargar mensajes:', err);
        this.isLoadingChat.set(false);
      }
    });
  }

  cargarMasMensajes(): void {
    if (!this.activeConversacionId || this.isLoadingMore() || !this.hasMorePages()) {
      return;
    }

    const container = this.messagesContainer?.nativeElement;
    const scrollHeightBefore = container ? container.scrollHeight : 0;

    const nextPage = this.currentPage() + 1;
    this.isLoadingMore.set(true);

    this.chatService.obtenerMensajes(this.activeConversacionId, nextPage, 20).subscribe({
      next: (page: SpringPage<ChatMensajeResponse>) => {
        const masViejos = [...(page.content || [])].reverse();
        this.mensajes.update((prev) => [...masViejos, ...prev]);
        this.currentPage.set(nextPage);
        this.hasMorePages.set(!page.last && (page.content?.length ?? 0) > 0);
        this.isLoadingMore.set(false);

        if (container) {
          setTimeout(() => {
            const scrollHeightAfter = container.scrollHeight;
            container.scrollTop = scrollHeightAfter - scrollHeightBefore;
          }, 0);
        }
      },
      error: (err) => {
        console.error('Error al cargar mensajes anteriores:', err);
        this.isLoadingMore.set(false);
      }
    });
  }

  private recargarMensajesRecientes(id: number): void {
    this.chatService.obtenerMensajes(id, 0, 20).subscribe({
      next: (page: SpringPage<ChatMensajeResponse>) => {
        const nuevos = [...(page.content || [])].reverse();
        this.fusionarMensajes(nuevos);
        this.scrollHaciaAbajo(false);
      },
      error: (err) => console.error('Error al sincronizar reconexión:', err)
    });
  }

  private procesarLecturaRecibida(lectura: ChatLecturaResponse): void {
    if (this.activeConversacionId === lectura.conversacionId) {
      // El cliente leyó los mensajes -> marcar todos los mensajes del agente como leídos
      this.mensajes.update((list) =>
        list.map((m) => (this.esMensajePropio(m) ? { ...m, leido: true } : m))
      );
    }
  }

  private procesarMensajeRecibido(mensaje: ChatMensajeResponse): void {
    // ESTRATEGIA DE NO DUPLICACIÓN: se muestra cuando llega por topic y se deduplica por ID
    const existe = this.mensajes().some((m) => m.id === mensaje.id);
    if (!existe) {
      this.mensajes.update((prev) => [...prev, mensaje]);

      if (mensaje.esSistema) {
        this.intentarParsearSnapshot(mensaje.contenido);
      }

      // Si el agente tiene este chat abierto y el mensaje proviene del cliente, marcarlo como leído inmediatamente
      if (!this.esMensajePropio(mensaje) && !mensaje.esSistema && this.activeConversacionId) {
        this.chatSocketService.marcarLeido(this.activeConversacionId);
      }

      this.scrollHaciaAbajo(false);
    }
  }

  private procesarCambioEstado(actualizado: ChatConversacionResponse): void {
    // Si es la conversación seleccionada, actualizar
    if (this.conversacionSeleccionada()?.id === actualizado.id) {
      this.conversacionSeleccionada.set(actualizado);
    }
    // Actualizar en la lista actual o recargar bandeja
    this.conversaciones.update((list) =>
      list.map((c) => (c.id === actualizado.id ? actualizado : c))
    );
    this.actualizarConteosGenerales();
  }

  private fusionarMensajes(recientes: ChatMensajeResponse[]): void {
    const mapa = new Map<number, ChatMensajeResponse>();
    for (const m of this.mensajes()) {
      mapa.set(m.id, m);
    }
    for (const m of recientes) {
      mapa.set(m.id, m);
    }
    const unificados = Array.from(mapa.values()).sort(
      (a, b) => new Date(a.fechaEnvio).getTime() - new Date(b.fechaEnvio).getTime()
    );
    this.mensajes.set(unificados);
  }

  private extraerSnapshotDeMensajes(mensajes: ChatMensajeResponse[]): void {
    for (const m of mensajes) {
      if (m.esSistema && this.intentarParsearSnapshot(m.contenido)) {
        break;
      }
    }
  }

  private intentarParsearSnapshot(contenido: string): boolean {
    try {
      const data = JSON.parse(contenido);
      if (data && (data.numeroPedido || data.total !== undefined)) {
        this.pedidoSnapshot.set(data as PedidoSnapshot);
        return true;
      }
    } catch {}
    return false;
  }

  tomarChat(conversacion: ChatConversacionResponse, event?: Event): void {
    if (event) event.stopPropagation();
    if (this.isAsignando()) return;

    this.isAsignando.set(conversacion.id);

    this.chatService.asignar(conversacion.id).subscribe({
      next: (actualizada) => {
        this.isAsignando.set(null);
        // Abrir inmediatamente la conversación tomada
        this.conversacionSeleccionada.set(actualizada);
        this.abrirConversacionPorId(actualizada.id);
        // Recargar bandeja y conteos
        this.actualizarConteosGenerales();
        this.cargarBandeja();
      },
      error: (err) => {
        this.isAsignando.set(null);
        console.error('Error al tomar chat (carrera de concurrencia):', err);
        const msg =
          err.error?.mensaje ||
          err.error?.message ||
          'El chat ya fue tomado por otro agente o no está disponible.';
        this.errorMsg.set(msg);
        // Recargar la bandeja porque otro agente ganó la carrera
        this.actualizarConteosGenerales();
        this.cargarBandeja();
      }
    });
  }

  enviar(): void {
    const texto = this.nuevoMensajeTexto.trim();
    if (!texto || !this.activeConversacionId || this.estaCerrado()) {
      return;
    }

    const enviada = this.chatSocketService.enviarMensaje(this.activeConversacionId, texto);
    if (enviada) {
      this.nuevoMensajeTexto = '';
    } else {
      this.errorMsg.set('No hay conexión con el servidor.');
    }
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.enviar();
    }
  }

  cerrarConversacion(): void {
    if (!this.activeConversacionId || this.isClosing() || this.estaCerrado()) {
      return;
    }

    if (!confirm('¿Confirmas que deseas cerrar y finalizar esta conversación de soporte?')) {
      return;
    }

    this.isClosing.set(true);
    this.chatService.cerrar(this.activeConversacionId).subscribe({
      next: (actualizada) => {
        this.isClosing.set(false);
        this.conversacionSeleccionada.set(actualizada);
        this.actualizarConteosGenerales();
        this.cargarBandeja();
      },
      error: (err) => {
        this.isClosing.set(false);
        console.error('Error al cerrar conversación:', err);
        const msg = err.error?.mensaje || err.error?.message || 'No se pudo cerrar la conversación.';
        this.errorMsg.set(msg);
      }
    });
  }

  // Helpers de presentación
  conversacionesFiltradas(): ChatConversacionResponse[] {
    const txt = this.filtroTexto.trim().toLowerCase();
    if (!txt) return this.conversaciones();

    return this.conversaciones().filter(
      (c) =>
        c.numeroPedido?.toLowerCase().includes(txt) ||
        c.nombreCliente?.toLowerCase().includes(txt)
    );
  }

  estaCerrado(): boolean {
    return this.conversacionSeleccionada()?.estado === 'CERRADA';
  }

  esMensajePropio(m: ChatMensajeResponse): boolean {
    if (m.esSistema) return false;
    // En la consola de soporte (agente/admin), los mensajes con rol ADMIN o EMPLEADO son del staff
    return m.rolRemitente === 'ADMIN' || m.rolRemitente === 'EMPLEADO';
  }

  esSnapshotMensaje(m: ChatMensajeResponse): boolean {
    if (!m.esSistema) return false;
    try {
      const parsed = JSON.parse(m.contenido);
      return !!(parsed && parsed.numeroPedido);
    } catch {
      return false;
    }
  }

  calcularTiempoTranscurrido(fechaStr: string): string {
    if (!fechaStr) return '';
    const diffMs = Date.now() - new Date(fechaStr).getTime();
    const diffMin = Math.floor(diffMs / 60000);

    if (diffMin < 1) return 'Ahora';
    if (diffMin < 60) return `Hace ${diffMin}m`;
    const diffHoras = Math.floor(diffMin / 60);
    if (diffHoras < 24) return `Hace ${diffHoras}h`;
    const diffDias = Math.floor(diffHoras / 24);
    return `Hace ${diffDias}d`;
  }

  obtenerIniciales(nombre?: string | null): string {
    if (!nombre) return 'AG';
    const partes = nombre.trim().split(/\s+/);
    if (partes.length >= 2) {
      return (partes[0][0] + partes[1][0]).toUpperCase();
    }
    return nombre.substring(0, 2).toUpperCase();
  }

  private scrollHaciaAbajo(force: boolean = false): void {
    if (!isPlatformBrowser(this.platformId)) return;
    setTimeout(() => {
      const el = this.messagesContainer?.nativeElement;
      if (el) {
        el.scrollTop = el.scrollHeight;
      }
    }, force ? 60 : 0);
  }
}
