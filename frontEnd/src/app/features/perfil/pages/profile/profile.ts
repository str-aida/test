import { Component } from '@angular/core';
import { ProfileFormComponent } from '../../components/profile-form/profile-form';
import { ChangePasswordFormComponent } from "../../components/change-password-form/change-password-form";

@Component({
  selector: 'app-profile',
  imports: [ProfileFormComponent, ChangePasswordFormComponent],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class ProfileComponent {}