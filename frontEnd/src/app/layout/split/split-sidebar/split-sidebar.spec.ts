import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SplitSidebar } from './split-sidebar';

describe('SplitSidebar', () => {
  let component: SplitSidebar;
  let fixture: ComponentFixture<SplitSidebar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SplitSidebar],
    }).compileComponents();

    fixture = TestBed.createComponent(SplitSidebar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
