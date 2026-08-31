import { Component } from '@angular/core';
import { HeroComponent } from './sections/hero/hero';
import { HowItWorksComponent } from './sections/how-it-works/how-it-works';
import { CtaComponent } from './sections/cta/cta';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeroComponent, HowItWorksComponent, CtaComponent],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class HomeComponent {}
