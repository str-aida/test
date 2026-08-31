import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { LucideBell } from '@lucide/angular';
import { ProfileService } from '../../core/services/profile.service';
import { TopbarUser } from '../models/topbar-user.model';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-topbar',
  imports: [LucideBell],
  templateUrl: './topbar.html',
  styleUrl: './topbar.scss',
})
export class TopbarComponent implements OnInit {

  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  protected title = '';
  private readonly profileService = inject(ProfileService);
  private readonly cdr = inject(ChangeDetectorRef);
  
  protected user?: TopbarUser;

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
        this.user = {
          name: `${profile.nombre} ${profile.apellido}`,
          role: profile.rol
        };
        this.cdr.detectChanges();
      }
    })
  }

  get initials(): string {
    if (!this.user) {
      return '';
    }
    const [nombre, apellido] = this.user.name.split(' ');
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
