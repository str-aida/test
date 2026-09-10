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
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import {
  LucideArrowLeft,
  LucideCheck,
  LucideCheckCheck,
  LucideSend,
  LucidePackage,
  LucideLock,
  LucideShield,
  LucideMessageSquare,
  LucideAlertCircle,
  LucidePaperclip,
  LucideRefreshCw,
  LucideX,
  LucideExternalLink,
  LucideSlidersHorizontal
} from '@lucide/angular';
import { ChatService, SpringPage } from '../../../../core/services/chat.service';
import { ChatSocketService } from '../../../../core/services/chat-socket.service';
import {
  ChatConversacionResponse,
  ChatLecturaResponse,
  ChatMensajeResponse,
  PedidoSnapshot
} from '../../../../core/models/chat.models';

interface ChatItemHistorial {
  id: number;
  pedidoId: number;
  numeroPedido: string;
  estado: string;
  fecha: string;
}

@Component({
  selector: 'app-chat-cliente',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    DatePipe,
    DecimalPipe,
    LucideArrowLeft,
    LucideCheck,
    LucideCheckCheck,
    LucideSend,
    LucidePackage,
    LucideLock,
    LucideShield,
    LucideMessageSquare,
    LucideAlertCircle,
    LucidePaperclip,
    LucideRefreshCw,
    LucideX,
    LucideExternalLink,
    LucideSlidersHorizontal
  ],
  templateUrl: './chat-cliente.component.html',
  styleUrl: './chat-cliente.component.scss'
})
export class ChatClienteComponent implements OnInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly chatService = inject(ChatService);
  private readonly chatSocketService = inject(ChatSocketService);
  private readonly platformId = inject(PLATFORM_ID);

  @ViewChild('messagesContainer') private messagesContainer?: ElementRef<HTMLDivElement>;

  // Signals de estado
  conversacion = signal<ChatConversacionResponse | null>(null);
  mensajes = signal<ChatMensajeResponse[]>([]);
  pedidoSnapshot = signal<PedidoSnapshot | null>(null);
  conectado = signal<boolean>(false);
  estadoConexion = signal<'conectando' | 'conectado' | 'reconectando'>('conectando');
  isLoading = signal<boolean>(true);
  isLoadingMore = signal<boolean>(false);
  hasMorePages = signal<boolean>(false);
  currentPage = signal<number>(0);
  errorMsg = signal<string | null>(null);
  isClosing = signal<boolean>(false);
  chatsHistorial = signal<ChatItemHistorial[]>([]);
  showDrawer = signal<boolean>(false);
  showModalCierre = signal<boolean>(false);

  // Input model
  nuevoMensajeTexto = '';

  private subs: Subscription[] = [];
  private activeConversacionId: number | null = null;
  private readonly STORAGE_KEY = 'gestia_client_chats';

  ngOnInit(): void {
    this.cargarHistorialLocal();

    const paramSub = this.route.paramMap.subscribe((params) => {
      const idParam = params.get('id');
      if (idParam && !isNaN(+idParam)) {
        this.iniciarConversacion(+idParam);
      } else {
        this.manejarSinId();
      }
    });
    this.subs.push(paramSub);

    // Conexión Socket
    const connSub = this.chatSocketService.conectado.subscribe((connected) => {
      this.conectado.set(connected);
      if (connected) {
        this.estadoConexion.set('conectado');
      } else if (this.estadoConexion() === 'conectado') {
        this.estadoConexion.set('reconectando');
      }
    });
    this.subs.push(connSub);

    // Mensajes recibidos en tiempo real por STOMP topic
    const msgSub = this.chatSocketService.mensajes$.subscribe((mensaje) => {
      this.procesarMensajeRecibido(mensaje);
    });
    this.subs.push(msgSub);

    // Lecturas en tiempo real
    const lectSub = this.chatSocketService.lecturas$.subscribe((lectura) => {
      this.procesarLecturaRecibida(lectura);
    });
    this.subs.push(lectSub);

    // Cambios de estado en tiempo real
    const estadoSub = this.chatSocketService.estado$.subscribe((estadoActualizado) => {
      this.conversacion.set(estadoActualizado);
      this.actualizarHistorialItem(estadoActualizado);
    });
    this.subs.push(estadoSub);

    // Errores de negocio en tiempo real desde /user/queue/errors
    const errSub = this.chatSocketService.errores$.subscribe((err) => {
      this.errorMsg.set(err.mensaje);
      setTimeout(() => this.errorMsg.set(null), 6000);
    });
    this.subs.push(errSub);

    // Reconexión automática: recargar página 0 de mensajes
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

  private iniciarConversacion(id: number): void {
    this.activeConversacionId = id;
    this.isLoading.set(true);
    this.errorMsg.set(null);
    this.mensajes.set([]);
    this.currentPage.set(0);
    this.hasMorePages.set(false);

    // 1. Obtener detalle de la conversación
    this.chatService.obtenerDetalle(id).subscribe({
      next: (conv) => {
        this.conversacion.set(conv);
        this.guardarEnHistorial(conv);
        // 2. Conectar al WebSocket
        this.chatSocketService.conectar(id);
        // 3. Cargar primera página de mensajes (orden DESC desde backend)
        this.cargarMensajesIniciales(id);
      },
      error: (err) => {
        console.error('Error al cargar detalle de chat:', err);
        this.isLoading.set(false);
        const msg = err.error?.mensaje || err.error?.message || 'No se pudo acceder a la conversación.';
        this.errorMsg.set(msg);
      }
    });
  }

  private manejarSinId(): void {
    const historial = this.chatsHistorial();
    if (historial.length > 0) {
      // Redirigir al chat más reciente
      this.router.navigate(['/cliente/chat', historial[0].id], { replaceUrl: true });
    } else {
      this.isLoading.set(false);
    }
  }

  private cargarMensajesIniciales(id: number): void {
    this.chatService.obtenerMensajes(id, 0, 20).subscribe({
      next: (page: SpringPage<ChatMensajeResponse>) => {
        // En el backend vienen ordenados DESC (el index 0 es el más nuevo).
        // Los invertimos para mostrarlos cronológicamente en pantalla (arriba los viejos, abajo los nuevos).
        const cronologicos = [...(page.content || [])].reverse();
        this.mensajes.set(cronologicos);
        this.hasMorePages.set(!page.last && (page.content?.length ?? 0) > 0);
        this.currentPage.set(0);

        this.extraerSnapshotDeMensajes(cronologicos);

        this.isLoading.set(false);
        this.scrollHaciaAbajo(true);
      },
      error: (err) => {
        console.error('Error al cargar mensajes iniciales:', err);
        this.isLoading.set(false);
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
        const masViejosCronologicos = [...(page.content || [])].reverse();

        // Se anteponen al listado actual para no perder el orden cronológico
        this.mensajes.update((prev) => [...masViejosCronologicos, ...prev]);
        this.currentPage.set(nextPage);
        this.hasMorePages.set(!page.last && (page.content?.length ?? 0) > 0);
        this.isLoadingMore.set(false);

        // Mantener la posición relativa del scroll para una experiencia fluida
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
    // Al recuperar conexión o reingresar, reconsultar página 0 (marca leídos y sincroniza)
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
      // El agente leyó los mensajes -> marcar todos los mensajes propios del cliente como leídos
      this.mensajes.update((list) =>
        list.map((m) => (this.esMensajeCliente(m) ? { ...m, leido: true } : m))
      );
    }
  }

  private procesarMensajeRecibido(mensaje: ChatMensajeResponse): void {
    // ESTRATEGIA DE NO DUPLICACIÓN:
    // El backend hace broadcast del mensaje al topic incluyendo al emisor.
    // Solo agregamos el mensaje cuando llega por el topic, y verificamos por ID para evitar duplicados.
    const existe = this.mensajes().some((m) => m.id === mensaje.id);
    if (!existe) {
      this.mensajes.update((prev) => [...prev, mensaje]);

      if (mensaje.esSistema) {
        this.intentarParsearSnapshot(mensaje.contenido);
      }

      // Si el cliente está dentro del chat y el mensaje proviene del agente/staff, marcarlo leído en tiempo real
      if (!this.esMensajeCliente(mensaje) && !mensaje.esSistema && this.activeConversacionId) {
        this.chatSocketService.marcarLeido(this.activeConversacionId);
      }

      this.scrollHaciaAbajo(false);
    }
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
    } catch {
      // No era JSON de snapshot, es un mensaje de sistema plano
    }
    return false;
  }

  enviar(): void {
    const texto = this.nuevoMensajeTexto.trim();
    if (!texto || !this.activeConversacionId || this.estaCerrado()) {
      return;
    }

    const enviada = this.chatSocketService.enviarMensaje(this.activeConversacionId, texto);
    if (enviada) {
      // Limpiar input de inmediato. NO agregamos el mensaje a la vista aquí:
      // se espera el eco del topic para garantizar id y fecha de backend sin duplicación.
      this.nuevoMensajeTexto = '';
    } else {
      this.errorMsg.set('No hay conexión con el servidor. Intentando reconectar...');
    }
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.enviar();
    }
  }

  abrirModalCierre(): void {
    if (!this.activeConversacionId || this.isClosing() || this.estaCerrado()) return;
    this.showModalCierre.set(true);
  }

  cancelarCierre(): void {
    this.showModalCierre.set(false);
  }

  cerrarConversacion(): void {
    if (!this.activeConversacionId || this.isClosing() || this.estaCerrado()) return;
    this.showModalCierre.set(false);
    this.isClosing.set(true);
    this.chatService.cerrar(this.activeConversacionId).subscribe({
      next: (actualizada) => {
        this.isClosing.set(false);
        this.conversacion.set(actualizada);
        this.actualizarHistorialItem(actualizada);
      },
      error: (err) => {
        this.isClosing.set(false);
        console.error('Error al cerrar conversación:', err);
        const msg = err.error?.mensaje || err.error?.message || 'No se pudo cerrar la conversación.';
        this.errorMsg.set(msg);
      }
    });
  }

  toggleDrawer(): void {
    this.showDrawer.update((v) => !v);
  }

  seleccionarChat(id: number): void {
    this.showDrawer.set(false);
    if (this.activeConversacionId !== id) {
      this.router.navigate(['/cliente/chat', id]);
    }
  }

  estaCerrado(): boolean {
    return this.conversacion()?.estado === 'CERRADA';
  }

  esMensajeCliente(m: ChatMensajeResponse): boolean {
    if (m.esSistema) return false;
    const conv = this.conversacion();
    if (conv && m.remitenteId === conv.clienteId) return true;
    return m.rolRemitente === 'CLIENTE';
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

  private cargarHistorialLocal(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    try {
      const guardado = localStorage.getItem(this.STORAGE_KEY);
      if (guardado) {
        this.chatsHistorial.set(JSON.parse(guardado));
      }
    } catch {
      this.chatsHistorial.set([]);
    }
  }

  private guardarEnHistorial(conv: ChatConversacionResponse): void {
    if (!isPlatformBrowser(this.platformId)) return;
    const actual: ChatItemHistorial = {
      id: conv.id,
      pedidoId: conv.pedidoId,
      numeroPedido: conv.numeroPedido,
      estado: conv.estado,
      fecha: conv.fechaCreacion
    };
    const lista = this.chatsHistorial().filter((c) => c.id !== conv.id);
    lista.unshift(actual);
    const limitada = lista.slice(0, 10);
    this.chatsHistorial.set(limitada);
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(limitada));
    } catch {}
  }

  private actualizarHistorialItem(conv: ChatConversacionResponse): void {
    if (!isPlatformBrowser(this.platformId)) return;
    const lista = this.chatsHistorial().map((c) => {
      if (c.id === conv.id) {
        return { ...c, estado: conv.estado };
      }
      return c;
    });
    this.chatsHistorial.set(lista);
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(lista));
    } catch {}
  }
}
