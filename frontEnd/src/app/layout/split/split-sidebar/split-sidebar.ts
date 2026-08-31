//Angular
import { Component, Input } from '@angular/core';
//Lucide
import {
  LucideChartColumn,
  LucideKeyRound,
  LucideMapPin,
  LucidePhone,
  LucideShieldCheck,
  LucideShoppingCart,
  LucideStore,
  LucideUsers
} from '@lucide/angular';
//Models
import { SidebarIcon, SplitSidebarInfo } from './split-sidebar.model';
//Configuration
import { SIDEBAR_ICONS } from './split-sidebar-icons';

@Component({
  selector: 'app-split-sidebar',
  imports: [
    LucideStore,
    LucideMapPin,
    LucidePhone,
    LucideShieldCheck,
    LucideUsers,
    LucideKeyRound,
    LucideShoppingCart,
    LucideChartColumn
  ],
  templateUrl: './split-sidebar.html',
  styleUrl: './split-sidebar.scss',
})
export class SplitSidebarComponent {

  @Input({ required: true })
  info!: SplitSidebarInfo;

  // Este archivo une SPLIT-SIDEBAR.MODEL y SPLIT-SIDEBAR-ICONS.
  /** 
   * Devuelve el icono asociado a una tarjeta
   * según el tipo de sidebar y el índice de la tarjeta.
   */
  protected getCardIcon(index: number): SidebarIcon | undefined {

    return SIDEBAR_ICONS[this.info.type][index];
    //recibe info.type y devuelve SIDEBAR_ICONS mediante getIcardIcon()

  }

}