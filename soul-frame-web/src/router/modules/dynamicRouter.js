import router from '@/router'
import {useAuthStore} from '@/stores/auth'
import {profileRoute, rootRoute} from '@/router/modules/staticRouter.js'

export const dynamicRouter = async () => {
    const authStore = useAuthStore();
    await authStore.initRoutes();
    const routes = authStore.routes;
    routes.push(profileRoute);
    rootRoute.children = routes;
    router.addRoute(rootRoute);
}


