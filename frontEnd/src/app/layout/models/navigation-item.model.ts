export interface NavigationItem {
  id: string;
  label: string;
  route: string;
  children?: NavigationItem[];
}