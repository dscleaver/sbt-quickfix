# sbt-quickfix

This is an sbt plugin that generates a quickfix file that can be used with Vim.

Inspired by [Alois Cochard's blog post] on setting up quickfix using a bash wrapper around sbt..

## Usage

Add this to your `~/.sbt/0.13/plugins/plugins.sbt`:

    addSbtPlugin("com.dscleaver.sbt" % "sbt-quickfix" % "0.4.1")

*Note: this plugin was built for sbt 0.13 use version 0.3.1 for sbt 0.12*

## Vim Integration

If your vim is setup to use [Pathogen], then the vim-sbt plugin can be installed with this command:

    sbt installVimPlugin  #'sbt install-vim-plugin' for older versions of sbt...

This command will replace the plugin files whenever it is run, so it is recommended when upgrading versions of the plugin.

### Using the files in Vim

After a compile or test, assuming there was an error, a quickfix file will be created that Vim can use.

If your copy of Vim was compiled with [+clientserver] and you've enabled the `vim-enable-server` flag in your SBT project (default is `true`), then this plugin will automatically load the quickfix file into Vim once it's been created.

If you don't have [+clientserver] enabled in Vim, or you have disabled the `vim-enable-server` feature of the plugin, then you can use a Vim mapping to load the file.  By default the mapping is `<leader>ff`, but you can change this if you wish.

### Neovim support

If you use Neovim, you have to specify the vim executable as `nvim` in your project or in the global settings: `QuickFixKeys.vimExecutable := "nvim"`

### Default servername/socket

If you use a single instance of **Gvim** you don't need any further configuration to enjoy the integration, since **Gvim** defines `GVIM` as `servername` and this is **sbt-quickfix**'s default value.
Terminal **Vim** doesn't define a default `servername` so for using the integration you will need to define it explicitly (read the next section)

**Neovim** has replaced Vim's `serverclient` functionality with a more powerful RPC solution. **sbt-quickfix** can connect to **Nvim** using a socket file. **Nvim** doesn't define a default socket, every instance generates a random one instead.
You can find this socket with `:echo $NVIM_LISTEN_ADDRESS` then you can start `sbt` passing the value as a System Property (read the next section)

### Controlling servername and socket / working with multiple instances

If you want to define the `servername`/`socket` for more predictable behavior or you need it because you work in many projects at a time with multiple **Vim** instances, you can do it easily.

For **Vim** or **Gvim** you have to launch using the `--servername` argument like this: `vim --servername MYPROJECT ...`.
When you launch `sbt` you have to attach to the specific **Vim** instance using the `sbtquickfix.vim.servername` system property: `sbt -Dsbtquickfix.vim.servername=MYPROJECT ...`

For **Neovim** you can define the socket file as an environment variable before you start **Nvim**: `NVIM_LISTEN_ADDRESS=/tmp/nvim_my_project.sock nvim`.
When you launch `sbt` you have to attach to the specific **Nvim** instance using the `sbtquickfix.nvim.socket` system property: `sbt -Dsbtquickfix.nvim.socket=/tmp/nvim_my_project.sock  ...` (**sbt-quickfix** uses by default the socket `/tmp/nvim.sock`)

### Installation Location

By default the plugin will install to `~/.vim/bundle`. This plugin base directory can be added to your global config (e.g. `~/.sbt/0.13/global.sbt`) as follows:

   QuickFixKeys.pluginBaseDirectory := file("intended/directory") 
   
You can disable the vim-enable-server flag in the global.sbt as follows: 

   QuickFixKeys.vimEnableServer := false

### Previous Integration

The initial version of this plugin required that you add the following lines to your `~/.vimrc`:

    set errorformat=%E\ %#[error]\ %#%f:%l:\ %m,%-Z\ %#[error]\ %p^,%-C\ %#[error]\ %m
    set errorformat+=,%W\ %#[warn]\ %#%f:%l:\ %m,%-Z\ %#[warn]\ %p^,%-C\ %#[warn]\ %m
    set errorformat+=,%-G%.%#

    noremap <silent> <Leader>ff :cf target/quickfix/sbt.quickfix<CR>
    noremap <silent> <Leader>fn :cn<CR>

These lines should be removed from `~/.vimrc'

## To Do 

Items that I want to look into in the future:

* Possibly reformat log output for easier consumption
* Add ctags generation possibly turning this into a full vim plugin
* Allow vim server name to be overridden by system property

[Alois Cochard's blog post]: http://aloiscochard.blogspot.co.uk/2013/02/quick-bug-fixing-in-scala-with-sbt-and.html
[Pathogen]: https://github.com/tpope/vim-pathogen
[+clientserver]: http://vimhelp.appspot.com/remote.txt.html#clientserver
