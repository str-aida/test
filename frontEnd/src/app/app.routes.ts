import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home';
import { OnboardingComponent } from './features/onboarding/onboarding';
import { AuthComponent } from './features/auth/auth';
import { AppLayoutComponent } from './layout/app-layout/app-layout';
import { EmployeesListComponent } from './features/empleados/pages/employees-list/employees-list';
import { LoginComponent } from './features/auth/login/login';
import { ForgotPasswordComponent } from './features/auth/forgot-password/forgot-password';
import { ResetPasswordComponent } from './features/auth/reset-password/reset-password';
import { RegisterClientComponent } from './features/auth/register-client/register-client';
import { ProfileComponent } from './features/perfil/pages/profile/profile';
import { CategoriesListComponent } from './features/categorias/pages/categories-list/categories-list';
import { CategoryFormComponent } from './features/categorias/components/category-form/category-form';
import { ProductsListComponent } from './features/productos/pages/products-list/products-list';
import { ProductFormComponent } from './features/productos/components/product-form/product-form';
import { CatalogoComponent } from './features/cliente/pages/catalogo/catalogo.component';
import { InicioClienteComponent } from './features/cliente/pages/inicio/inicio.component';
import { CarritoComponent } from './features/cliente/pages/carrito/carrito.component';
import { CheckoutComponent } from './features/cliente/pages/checkout/checkout.component';
import { PedidosComponent } from './features/cliente/pages/pedidos/pedidos.component';
import { PedidoDetalleComponent } from './features/cliente/pages/pedido-detalle/pedido-detalle.component';
import { AuditoriaListComponent } from './features/auditoria/pages/auditoria-list/auditoria-list';
import { CuponesListComponent } from './features/cupones/pages/cupones-list/cupones-list';
import { MisCuponesComponent } from './features/cliente/pages/mis-cupones/mis-cupones';
import { PedidosListComponent } from './features/pedidos/pages/pedidos-list/pedidos-list';
import { PedidoDetalleAdminComponent } from './features/pedidos/pages/pedido-detalle-admin/pedido-detalle-admin';
import { PedidosEnCursoComponent } from './features/pedidos/pages/pedidos-en-curso/pedidos-en-curso';
import { ConfiguracionComponent } from './features/configuracion/configuracion';
import { AnaliticaDashboardComponent } from './features/analitica/pages/analitica-dashboard/analitica-dashboard';

export const routes: Routes = [

  { path: '', component: HomeComponent },

  { path: 'onboarding', component: OnboardingComponent },

  {
    path: '',
    component: AuthComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterClientComponent },
      { path: 'forgot-password', component: ForgotPasswordComponent },
      { path: 'reset-password', component: ResetPasswordComponent }
    ]
  },

  {
    path: 'admin',
    component: AppLayoutComponent,
    children: [
      { path: 'analitica', component: AnaliticaDashboardComponent, data: { title: 'Analítica y Métricas' } },
      { path: 'dashboard', redirectTo: 'analitica', pathMatch: 'full' },
      { path: 'personal', component: EmployeesListComponent, data: { title: 'Personal' } },
      { path: 'perfil', component: ProfileComponent, data: { title: 'Perfil' } },
      { path: 'categorias', component: CategoriesListComponent, data: { title: 'Categorías' } },
      {
        path: 'productos',
        data: { title: 'Productos' },
        children: [
          { path: '', component: ProductsListComponent },
          { path: 'nuevo', component: ProductFormComponent },
          { path: 'editar/:id', component: ProductFormComponent }
        ]
      },
      { path: 'pedidos/en-curso', component: PedidosEnCursoComponent, data: { title: 'Pedidos en Curso' } },
      { path: 'pedidos', component: PedidosListComponent, data: { title: 'Todos los Pedidos' } },
      { path: 'pedidos/:id', component: PedidoDetalleAdminComponent, data: { title: 'Detalle de Pedido' } },
      { path: 'cupones', component: CuponesListComponent, data: { title: 'Gestión de Cupones' } },
      { path: 'auditoria', component: AuditoriaListComponent, data: { title: 'Auditoría' } },
      { path: 'configuracion', component: ConfiguracionComponent, data: { title: 'Configuración' } },
      { path: '', redirectTo: 'personal', pathMatch: 'full' }
    ]
  },

  {
    path: 'empleado',
    component: AppLayoutComponent,
    children: [
      { path: 'perfil', component: ProfileComponent, data: { title: 'Perfil' } },
      { path: 'pedidos/en-curso', component: PedidosEnCursoComponent, data: { title: 'Pedidos en Curso' } },
      { path: 'pedidos', component: PedidosListComponent, data: { title: 'Todos los Pedidos' } },
      { path: 'pedidos/:id', component: PedidoDetalleAdminComponent, data: { title: 'Detalle de Pedido' } },
      { path: 'productos', component: ProductsListComponent, data: { title: 'Productos' } },
      { path: '', redirectTo: 'pedidos/en-curso', pathMatch: 'full' }
    ]
  },
  
  {
    path: 'cliente',
    component: AppLayoutComponent,
    children: [
      { path: 'inicio', component: InicioClienteComponent, data: { title: 'Bienvenido a Gestia' } },
      { path: 'productos', component: CatalogoComponent, data: { title: 'Productos' } },
      { path: 'catalogo', redirectTo: 'productos', pathMatch: 'full' },
      { path: 'carrito', component: CarritoComponent, data: { title: 'Mi Carrito' } },
      { path: 'checkout', component: CheckoutComponent, data: { title: 'Finalizar Compra' } },
      { path: 'pedidos', component: PedidosComponent, data: { title: 'Mis Pedidos' } },
      { path: 'pedidos/:id', component: PedidoDetalleComponent, data: { title: 'Detalle de Pedido' } },
      { path: 'cupones', component: MisCuponesComponent, data: { title: 'Mis Cupones' } },
      { path: 'perfil', component: ProfileComponent, data: { title: 'Perfil' } },
      { path: '', redirectTo: 'inicio', pathMatch: 'full' }
    ]
  },

  {
    path: 'cliente',
    component: AuthComponent,
    children: [
      { path: 'cuenta', component: LoginComponent }
    ]
  },

  { path: '**', redirectTo: '' }

];
