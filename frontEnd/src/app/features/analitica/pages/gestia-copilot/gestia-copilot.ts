import { Component, ElementRef, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideBot, LucideSend, LucideSparkles, LucideTriangleAlert, LucideUser,
  LucideRotateCcw, LucideMessageSquare } from '@lucide/angular';
import { CopilotMessage, CopilotService } from '../../../../core/services/copilot.service';

@Component({
  selector: 'app-gestia-copilot',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideBot, LucideSend, LucideSparkles,
    LucideTriangleAlert, LucideUser, LucideRotateCcw, LucideMessageSquare],
  templateUrl: './gestia-copilot.html',
  styleUrl: './gestia-copilot.scss'
})
export class GestiaCopilotComponent implements OnInit {
  @ViewChild('chatContainer') private chatContainer!: ElementRef<HTMLDivElement>;

  private readonly copilotService = inject(CopilotService);

  messages = signal<CopilotMessage[]>([]);
  inputPrompt = signal<string>('');
  isLoading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  quickPrompts: string[] = [
    '¿Cuáles son mis ventas totales y cuántos pedidos tengo?',
    '¿Cuáles son mis productos más vendidos?',
    '¿Quiénes son mis mejores clientes?',
    '¿Tengo productos con bajo stock?'
  ];

  ngOnInit(): void {
    // Initial welcome message from Gestia Copilot
    this.messages.set([
      {
        id: 'msg-welcome',
        sender: 'assistant',
        text: '¡Hola! Soy **Gestia Copilot**, tu asistente de inteligencia artificial de negocios.\n\nPuedes preguntarme sobre ventas, pedidos, clientes, productos o inventario. ¿En qué te puedo ayudar hoy?',
        timestamp: new Date()
      }
    ]);
  }

  selectQuickPrompt(promptText: string): void {
    this.inputPrompt.set(promptText);
    this.enviarMensaje();
  }

  limpiarConversacion(): void {
    this.copilotService.resetContext();
    this.errorMessage.set(null);
    this.messages.set([
      {
        id: 'msg-welcome-' + Date.now(),
        sender: 'assistant',
        text: 'Conversación reiniciada. ¿En qué puedo ayudarte ahora?',
        timestamp: new Date()
      }
    ]);
  }

  enviarMensaje(): void {
    const prompt = this.inputPrompt().trim();
    if (!prompt || this.isLoading()) {
      return;
    }

    this.errorMessage.set(null);

    const userMessage: CopilotMessage = {
      id: `usr-${Date.now()}`,
      sender: 'user',
      text: prompt,
      timestamp: new Date()
    };

    this.messages.update((prev) => [...prev, userMessage]);
    this.inputPrompt.set('');
    this.isLoading.set(true);
    this.scrollToBottom();

    this.copilotService.sendMessage(prompt).subscribe({
      next: (agentReply) => {
        const assistantMessage: CopilotMessage = {
          id: `ast-${Date.now()}`,
          sender: 'assistant',
          text: agentReply,
          timestamp: new Date()
        };

        this.messages.update((prev) => [...prev, assistantMessage]);
        this.isLoading.set(false);
        this.scrollToBottom();
      },
      error: (err: Error) => {
        this.errorMessage.set(err.message || 'Error al comunicarse con el asistente.');

        const errorMsgObj: CopilotMessage = {
          id: `err-${Date.now()}`,
          sender: 'assistant',
          text: err.message || 'Error al comunicarse con el asistente.',
          timestamp: new Date(),
          isError: true
        };

        this.messages.update((prev) => [...prev, errorMsgObj]);
        this.isLoading.set(false);
        this.scrollToBottom();
      }
    });
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.enviarMensaje();
    }
  }

  formatMarkdown(text: string): string {
    if (!text) return '';
    
    // Sanitize HTML basic special chars to prevent XSS
    let html = text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');

    // Bold text (**text** or __text__)
    html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    html = html.replace(/__(.*?)__/g, '<strong>$1</strong>');

    // Italic text (*text* or _text_)
    html = html.replace(/\*(.*?)\*/g, '<em>$1</em>');

    // Headers (### Header)
    html = html.replace(/^### (.*$)/gim, '<h4 class="copilot-h4">$1</h4>');
    html = html.replace(/^## (.*$)/gim, '<h3 class="copilot-h3">$1</h3>');
    html = html.replace(/^# (.*$)/gim, '<h2 class="copilot-h2">$1</h2>');

    // Bullet points (- or *)
    html = html.replace(/^\s*[-*]\s+(.*$)/gim, '<li class="copilot-li">$1</li>');

    // Line breaks
    html = html.replace(/\n/g, '<br>');

    return html;
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      if (this.chatContainer) {
        const el = this.chatContainer.nativeElement;
        el.scrollTop = el.scrollHeight;
      }
    }, 100);
  }
}