import { Component, inject } from '@angular/core';
import { ADMIN_NAVIGATION, CLIENT_NAVIGATION, EMPLOYEE_NAVIGATION } from '../data/sidebar-navigation';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { BrandComponent } from '../../shared/components/brand/brand';
import {
  LucideLayoutDashboard,
  LucideShoppingCart,
  LucidePackage,
  LucideShapes,
  LucideBriefcaseBusiness,
  LucideSettings,
  LucideLogOut,
  LucideCircleUser,
  LucideHouse,
  LucidePackageCheck,
  LucideUserRoundCheck,
  LucideShieldCheck,
  LucideTicket
} from '@lucide/angular';
import { AuthService } from '../../core/services/auth.service';
import { TokenService } from '../../core/services/token.service';
import { UserRole } from '../../core/models/enums/user-role.enum';
import { NavigationItem } from '../models/navigation-item.model';

@Component({
  selector: 'app-sidebar',
  imports: [
    RouterLink,
    RouterLinkActive,
    BrandComponent,
    LucideLayoutDashboard,
    LucideShoppingCart,
    LucidePackage,
    LucideShapes,
    LucideBriefcaseBusiness,
    LucideSettings,
    LucideCircleUser,
    LucideHouse,
    LucidePackageCheck,
    LucideUserRoundCheck,
    LucideShieldCheck,
    LucideTicket,
    LucideLogOut
  ],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export class SidebarComponent {

  private authService = inject(AuthService);
  private tokenService = inject(TokenService);
  private router = inject(Router);
  protected navigationItems: NavigationItem[] = [];

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        console.log(); // "Sesión cerrada correctamente"

        this.tokenService.removeToken();
        this.router.navigate(['/login']);
      },
      error: () => {
        // Si el backend responde con error,
        // igual se cierra la sesión local.
        this.tokenService.removeToken();
        this.router.navigate(['/login']);
      }
    });
  }

  // Método para determinar la navegación según el rol del usuario
  constructor() {

    switch (this.tokenService.getRole()) {

      case UserRole.ADMIN:
        this.navigationItems = ADMIN_NAVIGATION;
        break;

      case UserRole.EMPLEADO:
        this.navigationItems = EMPLOYEE_NAVIGATION;
        break;

      case UserRole.CLIENTE:
        this.navigationItems = CLIENT_NAVIGATION;
        break;

      default:
        this.navigationItems = [];

    }

  }

}
