import { Component, EventEmitter, inject, OnInit, Output, signal } from '@angular/core';
import { LucideMenu } from '@lucide/angular';
import { NotificacionesBellComponent } from './components/notificaciones-bell/notificaciones-bell';
import { ProfileService } from '../../core/services/profile.service';
import { TopbarUser } from '../models/topbar-user.model';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-topbar',
  imports: [LucideMenu, NotificacionesBellComponent],
  templateUrl: './topbar.html',
  styleUrl: './topbar.scss',
})
export class TopbarComponent implements OnInit {

  @Output() menuToggle = new EventEmitter<void>();
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly profileService = inject(ProfileService);
  protected title = '';
  protected user = signal<TopbarUser | undefined>(undefined);

  ngOnInit(): void {
    this.loadUser();

    this.router.events
    .pipe(filter(event => event instanceof NavigationEnd))
    .subscribe(() => this.updateTitle());

    this.updateTitle();
  }

  private loadUser(): void {
    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.user.set({
          name: `${profile.nombre} ${profile.apellido}`,
          role: profile.rol
        });
      }
    })
  }

  get initials(): string {
    const user = this.user();
    if (!user) {
      return '';
    }
    const [nombre, apellido] = user.name.split(' ');
    return `${nombre[0]}${apellido[0]}`.toUpperCase();
  }

  private updateTitle(): void {
    let route = this.activatedRoute;
    while (route.firstChild) {
      route = route.firstChild;
    }
    this.title = route.snapshot.data['title'] ?? '';
  }

}