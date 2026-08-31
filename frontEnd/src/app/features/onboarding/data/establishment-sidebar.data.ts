import { SplitSidebarInfo } from "../../../layout/split/split-sidebar/split-sidebar.model";

export const ESTABLISHMENT_SIDEBAR: SplitSidebarInfo = {

    type: 'establishment',
    eyebrow: "BIENVENIDO A GESTIA",
    title: "Configurá tu establecimiento en minutos",
    description: "Completá los datos básicos de tu negocio para comenzar a gestionar pedidos, productos y clientes.",
    cards: [
        {
            title: "Datos del negocio",
            description: "Nombre y razón social"
        },
        {
            title: "Ubicación",
            description: "Dirección del local"
        },
        {
            title: "Contacto",
            description: "Teléfono y email"
        }

    ],

    currentStep: 1,
    totalSteps: 2

};