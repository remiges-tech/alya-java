import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgxAlyavalidationComponent } from './ngx-alyavalidation.component';

describe('NgxAlyavalidationComponent', () => {
  let component: NgxAlyavalidationComponent;
  let fixture: ComponentFixture<NgxAlyavalidationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NgxAlyavalidationComponent]
    });
    fixture = TestBed.createComponent(NgxAlyavalidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
