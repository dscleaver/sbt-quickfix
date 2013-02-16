# sbt-quickfix

This is an sbt plugin that generates a quickfix file that can be used with Vim.

Inspired by [Alois Cochard's blog post] on setting up quickfix using a bash wrapper around sbt..

## Usage

Add this to your `~/.sbt/plugins/plugins.sbt`:

    addSbtPlugin("com.dscleaver.sbt" % "sbt-quickfix" % "0.1.0")

*Note: this plugin was built for sbt 0.12.*

## Vim Integration

Add the following lines to your `~/.vimrc`:

    set errorformat=%E\ %#[error]\ %#%f:%l:\ %m,%-Z\ %#[error]\ %p^,%-C\ %#[error]\ %m
    set errorformat+=,%W\ %#[warn]\ %#%f:%l:\ %m,%-Z\ %#[warn]\ %p^,%-C\ %#[warn]\ %m
    set errorformat+=,%-G%.%#
    
    noremap <silent> <Leader>ff :cf target/quickfix/sbt.quickfix<CR>
    noremap <silent> <Leader>fn :cn<CR>

After a compile simply hit `<Leader>ff` to jump to the first error and `<Leader>fn` to move to the next.

## To Do 

Items that I want to look into in the future:

* Setup a vim error format (or plugin file)
* Provide task to install error format (or plugin file)
* Possibly reformat log output for easier consumption
* Add ctags generation possibly turning this into a full vim plugin

[Alois Cochard's blog post]: http://aloiscochard.blogspot.co.uk/2013/02/quick-bug-fixing-in-scala-with-sbt-and.html
