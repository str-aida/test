import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { UserRole } from '../models/enums/user-role.enum';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private readonly platformId = inject(PLATFORM_ID);
  private readonly TOKEN_KEY = 'token';

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  saveToken(token: string): void {
    if (this.isBrowser()) {
      localStorage.setItem(this.TOKEN_KEY, token);
    }
  }

  getToken(): string | null {
    if (!this.isBrowser()) {
      return null;
    }
    return localStorage.getItem(this.TOKEN_KEY);
  }

  removeToken(): void {
    if (this.isBrowser()) {
      localStorage.removeItem(this.TOKEN_KEY);
    }
  }

  hasToken(): boolean {
    return !!this.getToken();
  }

  getRole(): UserRole | null {

    const token = this.getToken();

    if (!token) {
      return null;
    }

    try {

      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.rol as UserRole;

    } catch {

      return null;

    }

  }

}