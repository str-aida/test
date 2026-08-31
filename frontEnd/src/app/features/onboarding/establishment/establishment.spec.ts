import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Establishment } from './establishment';

describe('Establishment', () => {
  let component: Establishment;
  let fixture: ComponentFixture<Establishment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Establishment],
    }).compileComponents();

    fixture = TestBed.createComponent(Establishment);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
