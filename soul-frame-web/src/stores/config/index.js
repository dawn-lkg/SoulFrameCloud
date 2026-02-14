import {defineStore} from 'pinia'
import {getConfigByGroup} from '@/api/modules/config'

export const useConfigStore = defineStore('config', {
    state: () => ({
        systemConfig: {
            name: '',
            version: '',
            home: '',
            logo: '',
            loginTitle: '',
            loginSubTitle: '',
            footer: '',
        }
    }),
    actions: {
        async initConfig() {
            await this.getSystemConfig()
        },
        async getSystemConfig() {
            const data = await getConfigByGroup('system')
            const config = data.reduce((acc, curr) => {
                acc[curr.configKey] = curr.configValue
                return acc
            }, {})
            this.systemConfig.name = config['sys.name']
            this.systemConfig.home = config['sys.indexHome']
            this.systemConfig.version = config['sys.version']
            this.systemConfig.logo = config['sys.logo']
            this.systemConfig.loginTitle = config['sys.loginTitle']
            this.systemConfig.loginSubTitle = config['sys.loginSubTitle']
            this.systemConfig.footer = config['sys.footer']
        }
    },
    persist: true
})