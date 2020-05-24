import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { AmazonSharedModule } from 'app/shared/shared.module';
import { AmazonCoreModule } from 'app/core/core.module';
import { AmazonAppRoutingModule } from './app-routing.module';
import { AmazonHomeModule } from './home/home.module';
import { AmazonEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    AmazonSharedModule,
    AmazonCoreModule,
    AmazonHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    AmazonEntityModule,
    AmazonAppRoutingModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent]
})
export class AmazonAppModule {}
