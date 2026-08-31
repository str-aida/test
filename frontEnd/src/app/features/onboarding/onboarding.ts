import { Component } from '@angular/core';
import { SplitSidebarComponent } from '../../layout/split/split-sidebar/split-sidebar';
import { SplitSidebarInfo } from '../../layout/split/split-sidebar/split-sidebar.model';
import { EstablishmentComponent } from './establishment/establishment';
import { AdminComponent } from './admin/admin';
import { ESTABLISHMENT_SIDEBAR } from './data/establishment-sidebar.data';
import { ADMIN_SIDEBAR } from './data/admin-sidebar.data';
import { SplitLayoutComponent } from "../../layout/split/split-layout/split-layout";

@Component({
  selector: 'app-onboarding',
  imports: [SplitSidebarComponent, EstablishmentComponent, AdminComponent, SplitLayoutComponent],
  templateUrl: './onboarding.html',
  styleUrl: './onboarding.scss',
})
export class OnboardingComponent {

  currentStep = 1;

  /**
   * Información mostrada en el panel izquierdo
   * según el paso actual del onboarding.
   */
  protected get sidebarInfo(): SplitSidebarInfo {

    return this.currentStep === 1
      ? ESTABLISHMENT_SIDEBAR
      : ADMIN_SIDEBAR;

  }

  /**
   * Avanza al siguiente paso del onboarding.
   */
  nextStep(): void {

    this.currentStep = 2;

  }

}
