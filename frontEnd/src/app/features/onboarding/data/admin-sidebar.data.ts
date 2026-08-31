import { SplitSidebarInfo } from "../../../layout/split/split-sidebar/split-sidebar.model";

export const ADMIN_SIDEBAR: SplitSidebarInfo = {

    type: 'admin',
    eyebrow: "BIENVENIDO A GESTIA",
    title: "Creá tu cuenta de administrador",
    description: "El administrador tiene acceso total al sistema: podrá gestionar personal, productos, pedidos y configuraciones del sistema.",
    cards: [
        {
            title: "Acceso total",
            description: "Control completo de la plataforma"
        },
        {
            title: "Gestión de personal",
            description: "Invitá y administrá a tu equipo"
        },
        {
            title: "Cuenta segura",
            description: "Tus datos protegidos en todo momento"
        }

    ],

    currentStep: 2,
    totalSteps: 2

};