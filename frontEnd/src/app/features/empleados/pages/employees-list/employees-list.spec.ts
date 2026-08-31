import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeesListComponent } from './employees-list';

describe('EmployeesList', () => {
  let component: EmployeesListComponent;
  let fixture: ComponentFixture<EmployeesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployeesListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeesListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
